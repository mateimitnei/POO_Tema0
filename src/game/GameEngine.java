package game;

import fileio.Input;
import fileio.GameInput;
import fileio.StartGameInput;
import fileio.ActionsInput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

public class GameEngine {
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
        players[0] = new Player(1);
        players[1] = new Player(2);
    }

    public void start(GameInput game) {
        startGame = game.getStartGame();
        actions = game.getActions();
        table = new Table();
        playerTurn = startGame.getStartingPlayer();
        currentRound = 1;
        // Set heroes
        players[0].setHero(startGame.getPlayerOneHero());
        players[1].setHero(startGame.getPlayerTwoHero());
        // Shuffle decks
        int seed = startGame.getShuffleSeed();
        players[0].initDeck(input.getPlayerOneDecks().getDecks().get(
                startGame.getPlayerOneDeckIdx()), seed);
        players[1].initDeck(input.getPlayerTwoDecks().getDecks().get(
                startGame.getPlayerTwoDeckIdx()), seed);
    }

    /**
     * Applies actions to the input.
     * <p>
     * Implementation of the main game mechanics.
     * </p>
     */
    public final void play(ArrayNode output) {
        newRound();

        for (ActionsInput action : actions) {
            int playerIdx = action.getPlayerIdx() - 1;
            int handIdx = action.getHandIdx();
            ObjectNode actionOutput = objectMapper.createObjectNode();
            actionOutput.put("command", action.getCommand());
            switch (action.getCommand()) {
                // Debug commands
                case "getPlayerDeck":
                    actionOutput.put("playerIdx", playerIdx + 1);
                    actionOutput.set("output", players[playerIdx].mappedDeck(objectMapper));
                    break;
                case "getPlayerHand":
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
                // Action commands
                case "placeCard":
                    actionOutput.put("handIdx", handIdx);
                    if (players[playerTurn - 1].getHand().get(handIdx).getMana() > players[playerTurn - 1].mana) {
                        actionOutput.put("error", "Not enough mana to place card on table.");
                        break;
                    }
                    table.placeCard(playerTurn, players[playerTurn - 1].getHand().get(handIdx));
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
            output.add(actionOutput);
        }
    }

    public void newRound() {
        currentRound++;
        for (Player player : players) {
            player.drawCard();
            player.mana += currentRound;
        }
    }

    private int getPlayerTurn(StartGameInput startGame) {
        return startGame.getStartingPlayer();
    }
}
