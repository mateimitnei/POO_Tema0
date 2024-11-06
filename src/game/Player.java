package game;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fileio.CardInput;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Represents a player in the game.
 */
@Getter
public final class Player {
    @Setter
    private int mana;
    @Setter
    private int wins;
    @Setter
    private Hero hero;
    private ArrayList<Card> hand;
    private ArrayList<Card> deck;

    public Player() {
        wins = 0;
        hand = new ArrayList<>();
        deck = new ArrayList<>();
    }

    /**
     * Initializes the player with a hero, a shuffled deck and no mana.
     * @param heroCard the hero card
     * @param inDeck the deck
     * @param seed the seed to shuffle the deck
     */
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

    /**
     * Draws a card from the deck and adds it to the hand.
     * Deletes the card from the deck.
     */
    public void drawCard() {
        if (!deck.isEmpty()) {
            hand.add(deck.get(0));
            deck.remove(0);
        }
    }

    /**
     * Maps the player's cards in hand into a JSON array.
     * Uses {@link Card#mappedCard} to map each card.
     * @param objectMapper the object mapper
     * @return the JSON array
     */
    public ArrayNode mappedHand(final ObjectMapper objectMapper) {
        ArrayNode handArray = objectMapper.createArrayNode();
        for (Card card : hand) {
            handArray.add(card.mappedCard(objectMapper));
        }
        return handArray;
    }

    /**
     * Maps the player's deck into a JSON array.
     * Uses {@link Card#mappedCard} to map each card.
     * @param objectMapper the object mapper
     * @return the JSON array
     */
    public ArrayNode mappedDeck(final ObjectMapper objectMapper) {
        ArrayNode deckArray = objectMapper.createArrayNode();
        for (Card card : deck) {
            deckArray.add(card.mappedCard(objectMapper));
        }
        return deckArray;
    }
}
