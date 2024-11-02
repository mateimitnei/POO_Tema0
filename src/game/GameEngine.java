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
    public static final int MAX_MANA = 10;
    private final Input input;
    private final ObjectMapper objectMapper;
    private final Player[] players;
    private Table table;
    private StartGameInput startGame;
    private ArrayList<ActionsInput> actions;
    private int playerTurn;
    private int currentRound;

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
    public final void play(final ArrayNode output, final int i) {
        // System.out.println("\n#### GAME " + i + " ####\n");
        int playerIdx;
        int handIdx;
        boolean addToOutput;

        for (ActionsInput action : actions) {
            playerIdx = action.getPlayerIdx() - 1;
            handIdx = action.getHandIdx();
            addToOutput = true;

            ObjectNode actionOutput = objectMapper.createObjectNode();
            actionOutput.put("command", action.getCommand());
            switch (action.getCommand()) {
                // Action commands
                case "endPlayerTurn":
                    playerTurn = (playerTurn == 1) ? 2 : 1;
                    if (playerTurn == startGame.getStartingPlayer()) {
                        newRound();
                    }
                    addToOutput = false;
                    break;
                case "placeCard":
                    // System.out.println("Player" + playerTurn + " -" + handIdx + ": "+ players[playerTurn - 1].getHand().get(handIdx).getMana() + " / " + players[playerTurn - 1].mana);
                    if (players[playerTurn - 1].getHand().get(handIdx).getMana()
                            > players[playerTurn - 1].mana) {
                        actionOutput.put("handIdx", handIdx);
                        actionOutput.put("error", "Not enough mana to place card on table.");
                        break;
                    }
                    boolean placed = table.placeCard(playerTurn, players[playerTurn - 1].getHand().get(handIdx));
                    if (!placed) {
                        actionOutput.put("handIdx", handIdx);
                        actionOutput.put("error", "Cannot place card on table since row is full.");
                        break;
                    }
                    players[playerTurn - 1].mana -= players[playerTurn - 1].getHand().get(handIdx).getMana();
                    players[playerTurn - 1].getHand().remove(handIdx);
                    // System.out.println("   !!!! Total: " + players[playerTurn - 1].mana);
                    addToOutput = false;
                    break;
                case "cardUsesAttack":

                    break;
                // Debug commands
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

    public void newRound() {
        currentRound++;
        for (Player player : players) {
            player.drawCard();
            player.mana += (currentRound > 10) ? MAX_MANA : currentRound;
        }
        // System.out.println("_________\nMana: + " + ((currentRound > 10) ? MAX_MANA : currentRound) + "\n---------");
    }
}
