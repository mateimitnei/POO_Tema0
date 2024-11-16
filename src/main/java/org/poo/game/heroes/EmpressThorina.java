package org.poo.game.heroes;

import org.poo.fileio.CardInput;
import org.poo.game.Card;
import org.poo.game.Hero;
import org.poo.game.Player;

import java.util.ArrayList;

public class EmpressThorina extends Hero {
    /**
     * Constructor for the hero. Sets health to 30.
     * @param card a card from the input
     */
    public EmpressThorina(final CardInput card) {
        super(card);
    }

    @Override
    public String ability(final int targetRow, final int playerTurn, final ArrayList<ArrayList<Card>> rows) {
        if ((playerTurn == 1 && targetRow / 2 == 1)
                || (playerTurn == 2 && targetRow / 2 == 0)) {
            return "Selected row does not belong to the enemy.";
        }
        int highestHp = -1;
        int idx = -1;
        for (int i = 0; i < rows.get(targetRow).size(); i++) {
            if (rows.get(targetRow).get(i).getHp() > highestHp) {
                highestHp = rows.get(targetRow).get(i).getHp();
                idx = i;
            }
        }
        if (idx != -1) {
            rows.get(targetRow).remove(idx);
        }
        return null;
    }
}
