package org.poo.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

/**
 * Represents a card in the game.
 */
@Getter
public class Card {
    private final String name;
    private final String description;
    private final ArrayList<String> colors;
    @Setter
    private int hp;
    private final int mana;
    @Setter
    private int attack;
    @Setter
    private boolean frozen;
    @Setter
    private boolean usedAttack;

    /**
     * Constructor for the card.
     * @param card a card from the input
     */
    public Card(final CardInput card) {
        name = card.getName();
        description = card.getDescription();
        colors = card.getColors();
        hp = card.getHealth();
        mana = card.getMana();
        attack = card.getAttackDamage();
        usedAttack = false;
        frozen = false;
    }

    /**
     * To be overridden by a class of a tank card.
     * @return true if the card is a tank, false otherwise
     */
    public boolean isTank() {
        return false;
    }

    /**
     * To be overridden by a class of a card with an ability.
     * @param x1 the row of the attacker card
     * @param x2 the row of the target card
     * @param target the card that the ability is used on
     * @param table the table of the game
     * @return error message or null
     */
    public String ability(final int x1, final int x2, final int y2, final Card target,
                          final Table table) {
        return null;
    }

    /**
     * Maps the card into a JSON object.
     * @param objectMapper the object mapper
     * @return the JSON object
     */
    public final ObjectNode mappedCard(final ObjectMapper objectMapper) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("mana", mana);
        cardNode.put("attackDamage", attack);
        cardNode.put("health", hp);
        cardNode.put("description", description);
        cardNode.putPOJO("colors", colors);
        cardNode.put("name", name);
        return cardNode;
    }
}
