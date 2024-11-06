package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a card in the game.
 */
@Getter
public class Card {
    public static final String[] TANKS = {"Warden", "Goliath"};
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
        this.name = card.getName();
        this.description = card.getDescription();
        this.colors = new ArrayList<>(card.getColors());
        this.hp = card.getHealth();
        this.mana = card.getMana();
        this.attack = card.getAttackDamage();
        usedAttack = false;
        frozen = false;
    }

    public final boolean isTank() {
        return Arrays.asList(TANKS).contains(name);
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
