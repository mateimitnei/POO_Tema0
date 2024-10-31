package game;

import fileio.Input;
import fileio.GameInput;
import fileio.StartGameInput;
import fileio.ActionsInput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
    private final Input input;
    private final ObjectMapper objectMapper;
    private final Player player1;
    private final Player player2;
    private Table table;
    private StartGameInput startGame;
    private ArrayList<ActionsInput> actions;
    private int currentTurn;
    private int currentRound;

    public GameEngine(final Input input) {
        this.input = input;
        objectMapper = new ObjectMapper();
        player1 = new Player(1);
        player2 = new Player(2);
    }

    public void start(GameInput game) {
        startGame = game.getStartGame();
        actions = game.getActions();
        table = new Table();
        currentTurn = 0;
        currentRound = 1;
        // Set heroes
        player1.setHero(startGame.getPlayerOneHero());
        player2.setHero(startGame.getPlayerTwoHero());
        // Shuffle decks
        int seed = startGame.getShuffleSeed();
        player1.initDeck(input.getPlayerOneDecks().getDecks().get(
                startGame.getPlayerOneDeckIdx()), seed);
        player2.initDeck(input.getPlayerTwoDecks().getDecks().get(
                startGame.getPlayerTwoDeckIdx()), seed);
    }

    /**
     * Applies actions to the input.
     * <p>
     * Implementation of the main game mechanics.
     * </p>
     */
    public final void play(ArrayNode output) {
        // Draw initial cards
        player1.drawCard();
        player2.drawCard();

        for (ActionsInput action : actions) {
            int idx = action.getPlayerIdx();
            ObjectNode actionOutput = objectMapper.createObjectNode();
            actionOutput.put("command", action.getCommand());
            switch (action.getCommand()) {
                case "getPlayerDeck":
                    actionOutput.put("playerIdx", idx);
                    actionOutput.set("output", idx == 1 ? player1.mappedDeck(objectMapper)
                            : player2.mappedDeck(objectMapper));
                    break;
                case "getPlayerHero":
                    actionOutput.put("playerIdx", idx);
                    actionOutput.set("output", idx == 1 ? player1.mappedHero(objectMapper)
                            : player2.mappedHero(objectMapper));
                    break;
                case "getPlayerTurn":
                    actionOutput.put("output", getPlayerTurn(startGame));
                    break;
                case "endPlayerTurn":

                    break;
                default:
                    break;
            }
            output.add(actionOutput);
        }
    }

    private int getPlayerTurn(StartGameInput startGame) {
        return startGame.getStartingPlayer();
    }
}
