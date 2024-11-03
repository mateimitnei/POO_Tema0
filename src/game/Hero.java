package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CardInput;

public class Hero extends Card {
    private static final int HERO_HP = 30;

    public Hero(final CardInput card) {
        super(card);
        setHp(HERO_HP);
    }

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
