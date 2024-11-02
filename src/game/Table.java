package game;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public final class Table {
    private static final int R_NUM = 4;
    private static final int C_NUM = 5;
    public static final String[] FRONT_ROW = {"Goliath", "Warden", "The Ripper", "Miraj"};
    // public static String[] backRow = {"Sentinel", "Berserker", "The Cursed One", "Disciple"};
    private final ArrayList<ArrayList<Card>> rows;

    public Table() {
        rows = new ArrayList<>(R_NUM);
        for (int i = 0; i < R_NUM; i++) {
            rows.add(new ArrayList<>());
        }
    }

    public Card getCard(final int x, final int y) {
        if (x >= 0 && x < R_NUM && y >= 0 && y < rows.get(x).size() && rows.get(x).get(y) != null){
            return rows.get(x).get(y);
        }
        return null;
    }

    public void resetPlayerCards(int playerTurn) {
        int r = 0;
        if (playerTurn == 1) {
            r = 2;
        }
        for (int i = r; i <= (r + 1); i++) {
            for (Card card : rows.get(i)) {
                card.alreadyAttacked = false;
                card.frozen = false;
            }
        }
    }

    public boolean placeCard(final int playerTurn, final Card card) {
        int placeRow = Arrays.asList(FRONT_ROW).contains(card.getName()) ? 2 : 3;
        // If it's player 2, place the card in the opposite row
        if (playerTurn == 2) {
            placeRow = (placeRow == 2) ? 1 : 0;
        }
        if (rows.get(placeRow).size() < C_NUM) {
            rows.get(placeRow).add(card);
            // System.out.println("Placed " + card.getName() + " on row " + placeRow);
            return true;
        }
        return false;
    }

    public String attack(final int x1, final int y1, final int x2, final int y2) {
        Card attackCard = getCard(x1, y1);
        Card targetCard = getCard(x2, y2);
        if (x1 / 2 == x2 / 2) {
            return "Attacked card does not belong to the enemy.";
        }
        if (attackCard.alreadyAttacked) {
            return "Attacker card has already attacked this turn.";
        }
        if (attackCard.frozen) {
            return "Attacker card is frozen.";
        }
        int targetFirstRow = 1;
        if (x2 == 2 || x2 == 3) {
            targetFirstRow = 2;
        }
        boolean tankAttacked = true;
        for (Card card : rows.get(targetFirstRow)) {
            if (card.isTank()) {
                if (targetCard == card) {
                    tankAttacked = true;
                    break;
                }
                tankAttacked = false;
            }
        }
        if (!tankAttacked) {
            return "Attacked card is not of type 'Tank'.";
        }
        targetCard.setHp(targetCard.getHp() - attackCard.getAttack());
        if (targetCard.getHp() <= 0) {
            rows.get(x2).remove(y2);
        }
        attackCard.alreadyAttacked = true;
        return null;
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
}
