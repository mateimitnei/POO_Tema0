package org.poo.game.heroes;

import org.poo.fileio.CardInput;
import org.poo.game.Card;
import org.poo.game.Hero;

import java.util.ArrayList;

public final class GeneralKocioraw extends Hero {
    /**
     * Constructor for the hero. Sets health to 30.
     * @param card a card from the input
     */
    public GeneralKocioraw(final CardInput card) {
        super(card);
    }

    @Override
    public String ability(final int targetRow, final int playerTurn,
                          final ArrayList<ArrayList<Card>> rows) {
        if ((playerTurn == 1 && targetRow / 2 == 0)
                || (playerTurn == 2 && targetRow / 2 == 1)) {
            return "Selected row does not belong to the current player.";
        }
        for (Card card : rows.get(targetRow)) {
            card.setAttack(card.getAttack() + 1);
        }
        return null;
    }
}
