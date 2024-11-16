package org.poo.game;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

/**
 * The game's table where cards are placed and actions are applied.
 */
@Getter
public final class Table {
    private static final int R_NUM = 4;
    private static final int C_NUM = 5;
    public static final String[] FRONT_ROW = {"Goliath", "Warden", "The Ripper", "Miraj"};
    public static final String[] ATTACK_ABILITIES = {"The Ripper", "Miraj", "The Cursed One"};
    private final ArrayList<ArrayList<Card>> rows;

    /**
     * Constructs a Table with [R_NUM] rows.
     */
    public Table() {
        rows = new ArrayList<>(R_NUM);
        for (int i = 0; i < R_NUM; i++) {
            rows.add(new ArrayList<>());
        }
    }

    /**
     * Gets the card at the specified coordinates.
     * @param x the row
     * @param y the column
     * @return the card at the specified coordinates, null if there is no card
     */
    public Card getCard(final int x, final int y) {
        if (x >= 0 && x < R_NUM && y >= 0 && y < rows.get(x).size()
                && rows.get(x).get(y) != null) {
            return rows.get(x).get(y);
        }
        return null;
    }

    /**
     * Resets the properties of the cards for the specified player and hero.
     * @param playerTurn the current player's turn
     * @param hero the hero of the current player
     */
    public void resetPlayerCardsProperties(final int playerTurn, final Card hero) {
        int r = 0;
        if (playerTurn == 1) {
            r = 2;
        }
        for (int i = r; i <= (r + 1); i++) {
            for (Card card : rows.get(i)) {
                card.setUsedAttack(false);
                card.setFrozen(false);
            }
        }
        hero.setUsedAttack(false);
    }

    /**
     * Places a card on the specified player's side of the table.
     * @param playerTurn the current player's turn
     * @param card the card to be placed
     * @return true if the card was placed successfully, false otherwise
     */
    public boolean placeCard(final int playerTurn, final Card card) {
        int placeRow = Arrays.asList(FRONT_ROW).contains(card.getName()) ? 2 : 3;
        // If it's player 2, place the card in the opposite row
        if (playerTurn == 2) {
            placeRow = (placeRow == 2) ? 1 : 0;
        }
        if (rows.get(placeRow).size() < C_NUM) {
            rows.get(placeRow).add(card);
            return true;
        }
        return false;
    }

    /**
     * Makes the attack of one card against another.
     * @param x1 the row of the attacking card
     * @param y1 the column of the attacking card
     * @param x2 the row of the target card
     * @param y2 the column of the target card
     * @return an error message if the attack is invalid, null otherwise
     */
    public String cardAttack(final int x1, final int y1, final int x2, final int y2) {
        Card attackCard = getCard(x1, y1);
        Card targetCard = getCard(x2, y2);
        if (x1 / 2 == x2 / 2) {
            return "Attacked card does not belong to the enemy.";
        }
        if (attackCard.isUsedAttack()) {
            return "Attacker card has already attacked this turn.";
        }
        if (attackCard.isFrozen()) {
            return "Attacker card is frozen.";
        }
        if (attacksIncorrectly(x2, targetCard)) {
            return "Attacked card is not of type 'Tank'.";
        }
        targetCard.setHp(targetCard.getHp() - attackCard.getAttack());
        if (targetCard.getHp() <= 0) {
            rows.get(x2).remove(y2);
        }
        attackCard.setUsedAttack(true);
        return null;
    }

    /**
     * Applies the ability of one card to a target card.
     * @param x1 the row of the card using the ability
     * @param y1 the column of the card using the ability
     * @param x2 the row of the target card
     * @param y2 the column of the target card
     * @return an error message if the ability is invalid, null otherwise
     */
    public String cardAbility(final int x1, final int y1, final int x2, final int y2) {
        Card attackCard = getCard(x1, y1);
        Card targetCard = getCard(x2, y2);
        if (attackCard.isFrozen()) {
            return "Attacker card is frozen.";
        }
        if (attackCard.isUsedAttack()) {
            return "Attacker card has already attacked this turn.";
        }
        String error = attackCard.ability(x1, x2, y2, targetCard, this);
        if (error != null) {
            return error;
        }
        attackCard.setUsedAttack(true);
        return null;
    }

    /**
     * Checks if the card attacks incorrectly.
     * Used by cardAttack and the ability methods from {@link org.poo.game.minions}.
     * @param x2 the row of the target card
     * @param targetCard the target card
     * @return true if the opponent has a tank card and the target is not a tank, false otherwise
     */
    public boolean attacksIncorrectly(final int x2, final Card targetCard) {
        int targetFirstRow = 1;
        if (x2 == 2 || x2 == 3) {
            targetFirstRow = 2;
        }
        boolean ok = false;
        for (Card card : rows.get(targetFirstRow)) {
            if (card.isTank()) {
                if (targetCard == card) {
                    return false;
                }
                ok = true;
            }
        }
        return ok;
    }

    /**
     * Makes the attack of one card against the enemy hero.
     * @param x the row of the attacking card
     * @param y the column of the attacking card
     * @return an error message if the attack is invalid, null otherwise
     */
    public String attackHero(final int x, final int y, final int playerTurn,
                             final Player[] players) {
        Card attackCard = getCard(x, y);
        if (attackCard.isFrozen()) {
            return "Attacker card is frozen.";
        }
        if (attackCard.isUsedAttack()) {
            return "Attacker card has already attacked this turn.";
        }
        int targetIdx = 0;
        int targetFirstRow = 2;
        if (playerTurn == 1) {
            targetIdx = 1;
            targetFirstRow = 1;
        }
        boolean hasTank = false;
        for (Card card : rows.get(targetFirstRow)) {
            if (card.isTank()) {
                hasTank = true;
            }
        }
        if (hasTank) {
            return "Attacked card is not of type 'Tank'.";
        }
        players[targetIdx].getHero().setHp(players[targetIdx].getHero().getHp()
                - attackCard.getAttack());
        if (players[targetIdx].getHero().getHp() <= 0) {
            return (playerTurn == 1) ? "1" : "2";
        }
        attackCard.setUsedAttack(true);
        return null;
    }

    /**
     * Applies the ability of the hero to the specified row.
     * @param targetRow the row to apply the ability to
     * @param playerTurn the current player's turn
     * @param players the players in the game
     * @return an error message if the ability is invalid, null otherwise
     */
    public String heroAbility(final int targetRow, final int playerTurn, final Player[] players) {
        Hero hero = players[playerTurn - 1].getHero();
        if (players[playerTurn - 1].getMana() < hero.getMana()) {
            return "Not enough mana to use hero's ability.";
        }
        if (hero.isUsedAttack()) {
            return "Hero has already attacked this turn.";
        }
        String error = hero.ability(targetRow, playerTurn, rows);
        if (error != null) {
            return error;
        }
        players[playerTurn - 1].setMana(players[playerTurn - 1].getMana() - hero.getMana());
        hero.setUsedAttack(true);
        return null;
    }

    /**
     * Maps the table into a JSON array.
     * Uses {@link Card#mappedCard} to map each card.
     * @param objectMapper the object mapper
     * @param onlyFrozenCards whether to map only the frozen cards
     * @return the JSON array
     */
    public ArrayNode mappedTable(final ObjectMapper objectMapper, final boolean onlyFrozenCards) {
        ArrayNode tableArray = objectMapper.createArrayNode();
        ArrayNode frozenArray = objectMapper.createArrayNode();
        for (ArrayList<Card> row : rows) {
            ArrayNode rowNode = objectMapper.createArrayNode();
            for (Card card : row) {
                if (onlyFrozenCards && card.isFrozen()) {
                    frozenArray.add(card.mappedCard(objectMapper));
                } else {
                    rowNode.add(card.mappedCard(objectMapper));
                }
            }
            tableArray.add(rowNode);
        }
        if (onlyFrozenCards) {
            return frozenArray;
        }
        return tableArray;
    }
}
