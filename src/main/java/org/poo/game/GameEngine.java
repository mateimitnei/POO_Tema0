package org.poo.game;

import org.poo.fileio.ActionsInput;
import org.poo.fileio.GameInput;
import org.poo.fileio.Input;
import org.poo.fileio.StartGameInput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

/**
 * The main game engine that handles the game logic and actions.
 * Uses all classes in the {@link org.poo.game} package to process the input.
 */
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
    private int gamesPlayed;
    private boolean addToOutput;
    private int handIdx;

    public GameEngine(final Input input) {
        this.input = input;
        objectMapper = new ObjectMapper();
        players = new Player[2];
        players[0] = new Player();
        players[1] = new Player();
        gamesPlayed = 0;
    }

    /**
     * Initializes the game with the given game input.
     * @param game the current game input data
     */
    public void start(final GameInput game) {
        startGame = game.getStartGame();
        actions = game.getActions();
        playerTurn = startGame.getStartingPlayer();
        int seed = startGame.getShuffleSeed();
        players[0].init(startGame.getPlayerOneHero(),
                input.getPlayerOneDecks().getDecks().get(startGame.getPlayerOneDeckIdx()), seed);
        players[1].init(startGame.getPlayerTwoHero(),
                input.getPlayerTwoDecks().getDecks().get(startGame.getPlayerTwoDeckIdx()), seed);
        table = new Table();
        currentRound = 0;
        newRound();
    }

    /**
     * Applies actions to the game.
     * @param output the output array node to store the results of the actions
     */
    public void play(final ArrayNode output) {

        for (ActionsInput action : actions) {

            int playerIdx = action.getPlayerIdx() - 1;
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
                case "useAttackHero":
                    useAttackHeroHandler(action, actionOutput);
                    break;
                case "useHeroAbility":
                    useHeroAbilityHandler(action, actionOutput);
                    break;
                // Debug commands
                case "getCardAtPosition":
                    getCardAtPositionHandler(action, actionOutput);
                    break;
                case "getCardsOnTable":
                    actionOutput.set("output", table.mappedTable(objectMapper, false));
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
                    actionOutput.set("output",
                            players[playerIdx].getHero().mappedHero(objectMapper));
                    break;
                case "getPlayerTurn":
                    actionOutput.put("output", playerTurn);
                    break;
                case "getPlayerMana":
                    actionOutput.put("playerIdx", playerIdx + 1);
                    actionOutput.put("output", players[playerIdx].getMana());
                    break;
                case "getFrozenCardsOnTable" :
                    actionOutput.set("output", table.mappedTable(objectMapper, true));
                    break;
                // Statistics commands
                case "getTotalGamesPlayed":
                    actionOutput.put("output", gamesPlayed);
                    break;
                case "getPlayerOneWins":
                    actionOutput.put("output", players[0].getWins());
                    break;
                case "getPlayerTwoWins":
                    actionOutput.put("output", players[1].getWins());
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
            player.setMana(player.getMana() + Math.min(currentRound, MAX_MANA));
        }
    }

    // Handlers for some of the switch cases:

    private void endPlayerTurnHandler() {
        table.resetPlayerCards(playerTurn, players[playerTurn - 1].getHero());
        playerTurn = (playerTurn == 1) ? 2 : 1;
        if (playerTurn == startGame.getStartingPlayer()) {
            newRound();
        }
        addToOutput = false;
    }

    /**
     * Handles the placement of a card on the table.
     * @param actionOutput the output node to store the result of the action
     */
    private void placeCardHandler(final ObjectNode actionOutput) {
        if (players[playerTurn - 1].getHand().get(handIdx).getMana()
                > players[playerTurn - 1].getMana()) {
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
        players[playerTurn - 1].setMana(players[playerTurn - 1].getMana()
                - players[playerTurn - 1].getHand().get(handIdx).getMana());
        players[playerTurn - 1].getHand().remove(handIdx);
        addToOutput = false;
    }

    /**
     * Handles the play of a card (attack or ability).
     * @param action the action input data
     * @param actionOutput the output node to store the result of the action
     * @param playType "attack" or "ability"
     */
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

    /**
     * Handles the attack on the enemy hero.
     * @param action the action input data
     * @param actionOutput the output node to store the result of the action
     */
    private void useAttackHeroHandler(final ActionsInput action, final ObjectNode actionOutput) {
        int x = action.getCardAttacker().getX();
        int y = action.getCardAttacker().getY();
        ObjectNode cardAttacker = objectMapper.createObjectNode();
        cardAttacker.put("x", x);
        cardAttacker.put("y", y);
        actionOutput.set("cardAttacker", cardAttacker);
        String output = table.attackHero(x, y, playerTurn, players);
        if (output != null) {
            if (output.equals("1")) {
                actionOutput.removeAll();
                actionOutput.put("gameEnded", "Player one killed the enemy hero.");
                players[0].setWins(players[0].getWins() + 1);
                gamesPlayed++;
                return;
            } else if (output.equals("2")) {
                actionOutput.removeAll();
                actionOutput.put("gameEnded", "Player two killed the enemy hero.");
                players[1].setWins(players[1].getWins() + 1);
                gamesPlayed++;
                return;
            }
            actionOutput.put("error", output);
            return;
        }
        addToOutput = false;
    }

    /**
     * Handles the info of a card on the table.
     * @param action the action input data
     * @param actionOutput the output node to store the result of the action
     */
    private void getCardAtPositionHandler(final ActionsInput action,
                                          final ObjectNode actionOutput) {
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

    /**
     * Handles the use of a hero's ability.
     * @param action the action input data
     * @param actionOutput the output node to store the result of the action
     */
    private void useHeroAbilityHandler(final ActionsInput action, final ObjectNode actionOutput) {
        actionOutput.put("affectedRow", action.getAffectedRow());
        String error = table.heroAbility(action.getAffectedRow(), playerTurn, players);
        if (error != null) {
            actionOutput.put("error", error);
            return;
        }
        addToOutput = false;
    }
}
