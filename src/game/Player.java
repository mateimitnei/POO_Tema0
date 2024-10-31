package game;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fileio.CardInput;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Getter @Setter
public final class Player {
    private int id;
    public int mana;
    private ArrayList<Card> hand;
    private ArrayList<Card> deck;
    private Hero hero;

    public Player(final int id) {
        this.id = id;
        mana = 0;
        hand = new ArrayList<>();
        deck = new ArrayList<>();
    }

    public void setHero(final CardInput card) {
        hero = new Hero(card);
    }

    public void initDeck(final ArrayList<CardInput> rawDeck, final int seed) {
        for (CardInput card : rawDeck) {
            deck.add(new Card(card));
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
            handArray.add(mappedCard(objectMapper, card));
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
            deckArray.add(mappedCard(objectMapper, card));
        }
        return deckArray;
    }

    public ObjectNode mappedCard(final ObjectMapper objectMapper, final Card card) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("mana", card.getMana());
        cardNode.put("attackDamage", card.getAttack());
        cardNode.put("health", card.getHp());
        cardNode.put("description", card.getDescription());
        cardNode.putPOJO("colors", card.getColors());
        cardNode.put("name", card.getName());
        return cardNode;
    }

    /**
     * Returns hero as a JSON object.
     *
     * @param objectMapper the object mapper
     */
    public ObjectNode mappedHero(final ObjectMapper objectMapper) {
        ObjectNode heroNode = objectMapper.createObjectNode();
        heroNode.put("mana", hero.getMana());
        heroNode.put("description", hero.getDescription());
        heroNode.putPOJO("colors", hero.getColors());
        heroNode.put("name", hero.getName());
        heroNode.put("health", hero.getHp());
        return heroNode;
    }
}
