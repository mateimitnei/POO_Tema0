package game;

import java.util.ArrayList;
import java.util.Arrays;

public final class Table {
    private static final int R_NUM = 4;
    private static final int C_NUM = 5;
    private ArrayList<ArrayList<Card>> rows;
    public static String[] frontRow = {"Goliath", "Warden", "The Ripper", "Miraj"};
    public static String[] backRow = {"Sentinel", "Berserker", "The Cursed One", "Disciple"};

    public Table() {
        rows = new ArrayList<>(R_NUM);
        for (int i = 0; i < R_NUM; i++) {
            rows.add(new ArrayList<>(C_NUM));
        }
    }

    public void placeCard(int playerIdx, final Card card) {
        int placeRow = Arrays.asList(frontRow).contains(card.getName()) ? 1 : 0;
        // If it's player 2, place the card in the opposite row
        if (playerIdx == 2) {
            placeRow = (placeRow == 0) ? 3 : 2;
        }
        if (rows.get(placeRow).size() < C_NUM) {
            rows.get(placeRow).add(card);
        }
    }

    public Card getCard(final int x, final int y) {
        if (x >= 0 && x < R_NUM && y >= 0 && y < C_NUM) {
            return rows.get(x).get(y);
        }
        return null;
    }
}
