package game;

import fileio.CardInput;

public class Hero extends Card {

    public Hero(CardInput card) {
        super(card);
        setHp(30);
    }
}
