package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;

public class InputProcessor {
    private final Input input;
    private final ObjectMapper objectMapper;

    public InputProcessor(Input input) {
        this.input = input;
        this.objectMapper = new ObjectMapper();
    }

    public ArrayNode applyActions() {
        ArrayNode output = objectMapper.createArrayNode();
        for (GameInput game : input.getGames()) {
            StartGameInput startGame = game.getStartGame();
            Random seed1 = new Random(startGame.getShuffleSeed());
            Random seed2 = new Random(startGame.getShuffleSeed());
            shuffleDecks(startGame, seed1, seed2);
            // Temporary! Simulates first card draw for both players
            ArrayList<CardInput> deckPlayerOne = input.getPlayerOneDecks().getDecks().get(game.getStartGame().getPlayerOneDeckIdx());
            ArrayList<CardInput> deckPlayerTwo = input.getPlayerTwoDecks().getDecks().get(game.getStartGame().getPlayerTwoDeckIdx());
            Players playerOne = new Players(deckPlayerOne.get(0), 1);
            Players playerTwo = new Players(deckPlayerTwo.get(0), 2);
            deckPlayerOne.remove(0);
            deckPlayerTwo.remove(0);

            for (ActionsInput action : game.getActions()) {
                ObjectNode actionOutput = objectMapper.createObjectNode();
                actionOutput.put("command", action.getCommand());
                switch (action.getCommand()) {
                    case "getPlayerDeck":
                        actionOutput.put("playerIdx", action.getPlayerIdx());
                        actionOutput.set("output", getPlayerDeck(action.getPlayerIdx(), game.getStartGame()));
                        break;
                    case "getPlayerHero":
                        actionOutput.put("playerIdx", action.getPlayerIdx());
                        actionOutput.set("output", getPlayerHero(action.getPlayerIdx(), game.getStartGame()));
                        break;
                    case "getPlayerTurn":
                        actionOutput.put("output", getPlayerTurn(game.getStartGame()));
                        break;
                  // default:
                  //     throw new IllegalArgumentException("Unknown command: " + action.getCommand());
                }
                output.add(actionOutput);
            }
        }
        return output;
    }
    private void shuffleDecks(StartGameInput startGame, Random seed1, Random seed2) {
        Collections.shuffle(input.getPlayerOneDecks().getDecks().get(startGame.getPlayerOneDeckIdx()), seed1);
        Collections.shuffle(input.getPlayerTwoDecks().getDecks().get(startGame.getPlayerTwoDeckIdx()), seed2);
    }

    private ArrayNode getPlayerDeck(int playerIdx, StartGameInput startGame) {
        DecksInput decks = (playerIdx == 1) ? input.getPlayerOneDecks() : input.getPlayerTwoDecks();
        List<CardInput> deck = decks.getDecks().get((playerIdx == 1) ? startGame.getPlayerOneDeckIdx() : startGame.getPlayerTwoDeckIdx());
        ArrayNode deckArray = objectMapper.createArrayNode();
        for (CardInput card : deck) {
            ObjectNode cardNode = objectMapper.createObjectNode();
            cardNode.put("mana", card.getMana());
            cardNode.put("attackDamage", card.getAttackDamage());
            cardNode.put("health", card.getHealth());
            cardNode.put("description", card.getDescription());
            cardNode.putPOJO("colors", card.getColors());
            cardNode.put("name", card.getName());
            deckArray.add(cardNode);
        }
        return deckArray;
    }

    private ObjectNode getPlayerHero(int playerIdx, StartGameInput startGame) {
        CardInput hero = (playerIdx == 1) ? startGame.getPlayerOneHero() : startGame.getPlayerTwoHero();
        ObjectNode heroNode = objectMapper.createObjectNode();
        heroNode.put("mana", hero.getMana());
        heroNode.put("description", hero.getDescription());
        heroNode.putPOJO("colors", hero.getColors());
        heroNode.put("name", hero.getName());
        heroNode.put("health", 30); // Assuming the health is always 30 for heroes
        return heroNode;
    }

    private int getPlayerTurn(StartGameInput startGame) {
        return startGame.getStartingPlayer();
    }
}
