package game;

import fileio.Input;
import fileio.GameInput;
import fileio.StartGameInput;
import fileio.ActionsInput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Arrays;

public class GameEngine {
    private final Input input;
    private final ObjectMapper objectMapper;
    private Player[] players;
    private Table table;
    private StartGameInput startGame;
    private ArrayList<ActionsInput> actions;
    public static final String[] noOutput = {"endPlayerTurn", "placeCard", "useHeroAbility",
            "useAttackHero", "cardUsesAbility", "cardUsesAttack"};
    public static final int MAX_MANA = 10;
    private int playerTurn;
    private int currentRound;

    public GameEngine(final Input input) {
        this.input = input;
        objectMapper = new ObjectMapper();
        players = new Player[2];
        players[0] = new Player(1);
        players[1] = new Player(2);
    }

    public void start(GameInput game) {
        startGame = game.getStartGame();
        actions = game.getActions();
        table = new Table();
        playerTurn = startGame.getStartingPlayer();
        currentRound = 0;
        // Set heroes
        players[0].setHero(startGame.getPlayerOneHero());
        players[1].setHero(startGame.getPlayerTwoHero());
        // Shuffle decks
        int seed = startGame.getShuffleSeed();
        players[0].initDeck(input.getPlayerOneDecks().getDecks().get(
                startGame.getPlayerOneDeckIdx()), seed);
        players[1].initDeck(input.getPlayerTwoDecks().getDecks().get(
                startGame.getPlayerTwoDeckIdx()), seed);
        players[0].mana = 0;
        players[1].mana = 0;
        newRound();
    }

    /**
     * Applies actions to the input.
     * <p>
     * Implementation of the main game mechanics.
     * </p>
     */
    public final void play(ArrayNode output, int i) {
        System.out.println("\n#### GAME "+ i +" ####\n");

        for (ActionsInput action : actions) {
            int playerIdx = action.getPlayerIdx() - 1;
            int handIdx = action.getHandIdx();
            if (Arrays.asList(noOutput).contains(action.getCommand())) {
                // Action commands
                switch (action.getCommand()) {
                    case "placeCard":
                        System.out.println("Player" + playerTurn + " -" + handIdx + ": " + players[playerTurn - 1].getHand().get(handIdx).getMana() + " / " + players[playerTurn - 1].mana);
                        if (players[playerTurn - 1].getHand().get(handIdx).getMana() > players[playerTurn - 1].mana) {
                            ObjectNode actionOutput = objectMapper.createObjectNode();
                            actionOutput.put("command", action.getCommand());
                            actionOutput.put("command", action.getCommand());
                            actionOutput.put("handIdx", handIdx);
                            actionOutput.put("error", "Not enough mana to place card on table.");
                            output.add(actionOutput);
                            break;
                        }
                        boolean placed = table.placeCard(playerTurn, players[playerTurn - 1].getHand().get(handIdx));
                        if (!placed) {
                            ObjectNode actionOutput = objectMapper.createObjectNode();
                            actionOutput.put("command", action.getCommand());
                            actionOutput.put("command", action.getCommand());
                            actionOutput.put("handIdx", handIdx);
                            actionOutput.put("error", "Cannot place card on table since row is full.");
                            output.add(actionOutput);
                            break;
                        }
                        players[playerTurn - 1].mana -= players[playerTurn - 1].getHand().get(handIdx).getMana();
                        players[playerTurn - 1].getHand().remove(handIdx);
                        System.out.println("   !!!! Total: " + players[playerTurn - 1].mana);
                        break;
                    case "endPlayerTurn":
                        playerTurn = (playerTurn == 1) ? 2 : 1;
                        if (playerTurn == startGame.getStartingPlayer()) {
                            newRound();
                        }
                        break;
                    default:
                        break;
                }
            } else {
                ObjectNode actionOutput = objectMapper.createObjectNode();
                actionOutput.put("command", action.getCommand());
                // Debug commands
                switch (action.getCommand()) {
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
                        actionOutput.set("output", players[playerIdx].mappedHero(objectMapper));
                        break;
                    case "getPlayerTurn":
                        actionOutput.put("output", playerTurn);
                        break;
                    default:
                        break;
                }
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
        System.out.println("_________\nMana: + " + ((currentRound > 10) ? MAX_MANA : currentRound) + "\n---------");
    }

    private int getPlayerTurn(StartGameInput startGame) {
        return startGame.getStartingPlayer();
    }
}
