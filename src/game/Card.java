package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
public class Card {
    private String name;
    private String description;
    private ArrayList<String> colors;
    @Setter
    private int hp;
    private int mana;
    @Setter
    private int attack;
    @Setter
    private boolean frozen;

    public Card(final CardInput card) {
        this.name = card.getName();
        this.description = card.getDescription();
        this.colors = new ArrayList<>(card.getColors());
        this.hp = card.getHealth();
        this.mana = card.getMana();
        this.attack = card.getAttackDamage();
        this.frozen = false; // Default
    }

    public ObjectNode mappedCard(final ObjectMapper objectMapper) {
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
