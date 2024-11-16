package org.poo.game;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.poo.fileio.CardInput;

import lombok.Getter;
import lombok.Setter;
import org.poo.game.heroes.EmpressThorina;
import org.poo.game.heroes.GeneralKocioraw;
import org.poo.game.heroes.KingMudface;
import org.poo.game.heroes.LordRoyce;
import org.poo.game.minions.Disciple;
import org.poo.game.minions.Goliath;
import org.poo.game.minions.Miraj;
import org.poo.game.minions.TheCursedOne;
import org.poo.game.minions.TheRipper;
import org.poo.game.minions.Warden;

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
     * Initializes the player with a hero, a shuffled deck and 0 mana. Also clears the hand.
     * @param heroCard the hero card
     * @param inputDeck the deck
     * @param seed the seed to shuffle the deck
     */
    public void init(final CardInput heroCard, final ArrayList<CardInput> inputDeck,
                     final int seed) {
        mana = 0;
        deck.clear();
        hand.clear();
        switch (heroCard.getName()) {
            case "Lord Royce":
                hero = new LordRoyce(heroCard);
                break;
            case "Empress Thorina":
                hero = new EmpressThorina(heroCard);
                break;
            case "King Mudface":
                hero = new KingMudface(heroCard);
                break;
            case "General Kocioraw":
                hero = new GeneralKocioraw(heroCard);
                break;
            default:
                break;
        }
        for (CardInput card : inputDeck) {
            switch (card.getName()) {
                case "Goliath":
                    deck.add(new Goliath(card));
                    break;
                case "Warden":
                    deck.add(new Warden(card));
                    break;
                case "The Ripper":
                    deck.add(new TheRipper(card));
                    break;
                case "Miraj":
                    deck.add(new Miraj(card));
                    break;
                case "The Cursed One":
                    deck.add(new TheCursedOne(card));
                    break;
                case "Disciple":
                    deck.add(new Disciple(card));
                    break;
                default:
                    deck.add(new Card(card));
                    break;
            }
        }
        Collections.shuffle(deck, new Random(seed));
    }

    /**
     * Draws a card from the deck and adds it to the hand.
     * Deletes the card from the deck.
     */
    public void drawCard() {
        if (!deck.isEmpty()) {
            hand.add(deck.getFirst());
            deck.removeFirst();
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
