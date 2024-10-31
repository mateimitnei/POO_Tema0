package game;

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
}
