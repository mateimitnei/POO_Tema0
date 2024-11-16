package org.poo.game.heroes;

import org.poo.fileio.CardInput;
import org.poo.game.Card;
import org.poo.game.Hero;

import java.util.ArrayList;

public final class LordRoyce extends Hero {
    /**
     * Constructor for the hero. Sets health to 30.
     * @param card a card from the input
     */
    public LordRoyce(final CardInput card) {
        super(card);
    }

    @Override
    public String ability(final int targetRow, final int playerTurn,
                          final ArrayList<ArrayList<Card>> rows) {
        if ((playerTurn == 1 && targetRow / 2 == 1)
                || (playerTurn == 2 && targetRow / 2 == 0)) {
            return "Selected row does not belong to the enemy.";
        }
        for (Card card : rows.get(targetRow)) {
            card.setFrozen(true);
        }
        return null;
    }
}
