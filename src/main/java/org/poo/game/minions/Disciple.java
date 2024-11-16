package org.poo.game.minions;

import org.poo.fileio.CardInput;
import org.poo.game.Card;
import org.poo.game.Table;

/**
 * Has a specific ability.
 */
public final class Disciple extends Card {
    public Disciple(final CardInput card) {
        super(card);
    }

    @Override
    public String ability(final int x1, final int x2, final int y2, final Card target,
                          final Table table) {
        if (x1 / 2 != x2 / 2) {
            return "Attacked card does not belong to the current player.";
        }
        target.setHp(target.getHp() + 2);
        return null;
    }
}
