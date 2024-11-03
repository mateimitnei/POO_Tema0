package game;

import fileio.Input;
import fileio.GameInput;
import fileio.StartGameInput;
import fileio.ActionsInput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public final class GameEngine {
    private static final int MAX_MANA = 10;
    private final Input input;
    private final ObjectMapper objectMapper;
    private final Player[] players;
    private Table table;
    private StartGameInput startGame;
    private ArrayList<ActionsInput> actions;
    private int playerTurn;
    private int currentRound;
    // Variables for the play method:
    private boolean addToOutput;
    private int playerIdx;
    private int handIdx;

    public GameEngine(final Input input) {
        this.input = input;
        objectMapper = new ObjectMapper();
        players = new Player[2];
        players[0] = new Player();
        players[1] = new Player();
    }

    public void start(final GameInput game) {
        startGame = game.getStartGame();
        actions = game.getActions();
        playerTurn = startGame.getStartingPlayer();
        int seed = startGame.getShuffleSeed();
        players[0].init(startGame.getPlayerOneHero(), input.getPlayerOneDecks().getDecks().get(
                startGame.getPlayerOneDeckIdx()), seed);
        players[1].init(startGame.getPlayerTwoHero(), input.getPlayerTwoDecks().getDecks().get(
                startGame.getPlayerTwoDeckIdx()), seed);
        table = new Table();
        currentRound = 0;
        newRound();
    }

    /**
     * Applies actions to the input.
     * <p>
     * Implementation of the main game mechanics.
     * </p>
     */
    public void play(final ArrayNode output, final int i) {

        for (ActionsInput action : actions) {

            playerIdx = action.getPlayerIdx() - 1;
            handIdx = action.getHandIdx();
            addToOutput = true;
            ObjectNode actionOutput = objectMapper.createObjectNode();
            actionOutput.put("command", action.getCommand());

            switch (action.getCommand()) {
                // Action commands
                case "endPlayerTurn":
                    endPlayerTurnHandler();
                    break;
                case "placeCard":
                    placeCardHandler(actionOutput);
                    break;
                case "cardUsesAttack":
                    cardPlayHandler(action, actionOutput, "attack");
                    break;
                case "cardUsesAbility":
                    cardPlayHandler(action, actionOutput, "ability");
                    break;
                // Debug commands
                case "getCardAtPosition":
                    getCardAtPositionHandler(action, actionOutput);
                    break;
                case "getCardsOnTable":
                    actionOutput.set("output", table.mappedTable(objectMapper));
                    break;
                case "getPlayerDeck":
                    actionOutput.put("playerIdx", playerIdx + 1);
                    actionOutput.set("output", players[playerIdx].mappedDeck(objectMapper));
                    break;
                case "getCardsInHand":
                    actionOutput.put("playerIdx", playerIdx + 1);
                    actionOutput.set("output", players[playerIdx].mappedHand(objectMapper));
                    break;
                case "getPlayerHero":
                    actionOutput.put("playerIdx", playerIdx + 1);
                    actionOutput.set("output", players[playerIdx].hero.mappedHero(objectMapper));
                    break;
                case "getPlayerTurn":
                    actionOutput.put("output", playerTurn);
                    break;
                case "getPlayerMana":
                    actionOutput.put("playerIdx", playerIdx + 1);
                    actionOutput.put("output", players[playerIdx].mana);
                    break;
                default:
                    break;
            }
            if (addToOutput) {
                output.add(actionOutput);
            }
        }
    }

    private void newRound() {
        currentRound++;
        for (Player player : players) {
            player.drawCard();
            player.mana += Math.min(currentRound, MAX_MANA);
        }
    }

    // Handlers for some of the switch cases:

    private void endPlayerTurnHandler() {
        playerTurn = (playerTurn == 1) ? 2 : 1;
        table.resetPlayerCards(playerTurn);
        if (playerTurn == startGame.getStartingPlayer()) {
            newRound();
        }
        addToOutput = false;
    }

    private void placeCardHandler(final ObjectNode actionOutput) {
        if (players[playerTurn - 1].getHand().get(handIdx).getMana()
                > players[playerTurn - 1].mana) {
            actionOutput.put("handIdx", handIdx);
            actionOutput.put("error", "Not enough mana to place card on table.");
            return;
        }
        boolean placed = table.placeCard(playerTurn,
                players[playerTurn - 1].getHand().get(handIdx));
        if (!placed) {
            actionOutput.put("handIdx", handIdx);
            actionOutput.put("error", "Cannot place card on table since row is full.");
            return;
        }
        players[playerTurn - 1].mana -= players[playerTurn - 1].getHand().get(handIdx).getMana();
        players[playerTurn - 1].getHand().remove(handIdx);
        addToOutput = false;
    }

    private void cardPlayHandler(final ActionsInput action, final ObjectNode actionOutput,
                                 final String playType) {
        int x1 = action.getCardAttacker().getX();
        int y1 = action.getCardAttacker().getY();
        int x2 = action.getCardAttacked().getX();
        int y2 = action.getCardAttacked().getY();
        ObjectNode cardAttacker = objectMapper.createObjectNode();
        cardAttacker.put("x", x1);
        cardAttacker.put("y", y1);
        ObjectNode cardAttacked = objectMapper.createObjectNode();
        cardAttacked.put("x", x2);
        cardAttacked.put("y", y2);
        actionOutput.set("cardAttacker", cardAttacker);
        actionOutput.set("cardAttacked", cardAttacked);
        if (playType.equals("attack")) {
            String error = table.attack(x1, y1, x2, y2);
            if (error != null) {
                actionOutput.put("error", error);
                return;
            }
        } else if (playType.equals("ability")) {
            String error = table.ability(x1, y1, x2, y2);
            if (error != null) {
                actionOutput.put("error", error);
                return;
            }
        }
        addToOutput = false;
    }

    private void getCardAtPositionHandler(final ActionsInput action, final ObjectNode actionOutput) {
        int x = action.getX();
        int y = action.getY();
        actionOutput.put("x", x);
        actionOutput.put("y", y);
        if (table.getCard(x, y) == null) {
            actionOutput.put("output", "No card available at that position.");
            return;
        }
        actionOutput.set("output", table.getCard(x, y).mappedCard(objectMapper));
    }
}
