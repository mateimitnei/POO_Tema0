package org.poo.game.minions;

import org.poo.fileio.CardInput;
import org.poo.game.Card;

public final class Warden extends Card {
    public Warden(final CardInput card) {
        super(card);
    }

    @Override
    public boolean isTank() {
        return true;
    }
}
