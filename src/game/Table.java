package game;

public final class Table {
    private static final int R_NUM = 4;
    private static final int C_NUM = 5;
    private Card[][] rows;

    public Table() {
        this.rows = new Card[R_NUM][C_NUM];
    }

    public void placeCard(final int x, final int y, final Card card) {
        if (x >= 0 && x < R_NUM && y >= 0 && y < C_NUM) {
            rows[x][y] = card;
        }
    }

    public Card getCard(final int x, final int y) {
        if (x >= 0 && x < R_NUM && y >= 0 && y < C_NUM) {
            return rows[x][y];
        }
        return null;
    }
}
