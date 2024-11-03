package game;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fileio.CardInput;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Getter
public final class Player {
    public int mana;
    public int wins;
    public Hero hero;
    private ArrayList<Card> hand;
    private ArrayList<Card> deck;

    public Player() {
        wins = 0;
        hand = new ArrayList<>();
        deck = new ArrayList<>();
    }

    public void init(final CardInput heroCard, final ArrayList<CardInput> inDeck, final int seed) {
        mana = 0;
        hero = new Hero(heroCard);
        deck.clear();
        hand.clear();
        for (CardInput card : inDeck) {
            this.deck.add(new Card(card));
        }
        Collections.shuffle(deck, new Random(seed));
    }

    public void drawCard() {
        if (!deck.isEmpty()) {
            hand.add(deck.get(0));
            deck.remove(0);
        }
    }

    /**
     * Returns hand as a JSON object.
     *
     * @param objectMapper the object mapper
     */
    public ArrayNode mappedHand(final ObjectMapper objectMapper) {
        ArrayNode handArray = objectMapper.createArrayNode();
        for (Card card : hand) {
            handArray.add(card.mappedCard(objectMapper));
        }
        return handArray;
    }

    /**
     * Returns deck as a JSON object.
     *
     * @param objectMapper the object mapper
     */
    public ArrayNode mappedDeck(final ObjectMapper objectMapper) {
        ArrayNode deckArray = objectMapper.createArrayNode();
        for (Card card : deck) {
            deckArray.add(card.mappedCard(objectMapper));
        }
        return deckArray;
    }
}
