package game;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class Table {
    private static final int R_NUM = 4;
    private static final int C_NUM = 5;
    public static String[] frontRow = {"Goliath", "Warden", "The Ripper", "Miraj"};
    // public static String[] backRow = {"Sentinel", "Berserker", "The Cursed One", "Disciple"};
    private final ArrayList<ArrayList<Card>> rows;

    public Table() {
        rows = new ArrayList<>(R_NUM);
        for (int i = 0; i < R_NUM; i++) {
            rows.add(new ArrayList<>());
        }
    }

    public boolean placeCard(final int playerTurn, final Card card) {
        int placeRow = Arrays.asList(frontRow).contains(card.getName()) ? 1 : 0;
        // If it's player 2, place the card in the opposite row
        if (playerTurn == 2) {
            placeRow = (placeRow == 0) ? 3 : 2;
        }
        if (rows.get(placeRow).size() < C_NUM) {
            rows.get(placeRow).add(card);
            System.out.println("Placed " + card.getName() + " on row " + placeRow);
            return true;
        }
        return false;
    }

    /**
     * Returns table as a JSON object.
     *
     * @param objectMapper the object mapper
     */
    public ArrayNode mappedTable(final ObjectMapper objectMapper) {
        ArrayNode tableArray = objectMapper.createArrayNode();
        for (ArrayList<Card> row : rows) {
            ArrayNode rowNode = objectMapper.createArrayNode();
            for (Card card : row) {
                rowNode.add(card.mappedCard(objectMapper));
            }
            tableArray.add(rowNode);
        }
        return tableArray;
    }

    public Card getCard(final int x, final int y) {
        if (x >= 0 && x < R_NUM && y >= 0 && y < C_NUM) {
            return rows.get(x).get(y);
        }
        return null;
    }
}
