package org.poo.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;

import java.util.ArrayList;

/**
 * Represents a player's hero in the game. Extends the card class.
 */
public class Hero extends Card {
    private static final int HERO_HP = 30;

    /**
     * Constructor for the hero. Sets health to 30.
     * @param card a card from the input
     */
    public Hero(final CardInput card) {
        super(card);
        setHp(HERO_HP);
    }

    /**
     * To be overridden by each hero's ability method.
     * @param targetRow the row to use ability on
     * @param playerTurn the player's turn (1 or 2)
     * @return error message or null
     */
    public String ability(final int targetRow, final int playerTurn,
                          final ArrayList<ArrayList<Card>> rows) {
        return null;
    }

    /**
     * Maps the hero into a JSON object.
     * @param objectMapper the object mapper
     * @return the JSON object
     */
    public final ObjectNode mappedHero(final ObjectMapper objectMapper) {
        ObjectNode heroNode = objectMapper.createObjectNode();
        heroNode.put("mana", super.getMana());
        heroNode.put("description", super.getDescription());
        heroNode.putPOJO("colors", super.getColors());
        heroNode.put("name", super.getName());
        heroNode.put("health", super.getHp());
        return heroNode;
    }
}
