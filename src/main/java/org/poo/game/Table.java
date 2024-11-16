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
     * Constructs a Table with initialized rows.
     */
    public Table() {
        rows = new ArrayList<>(R_NUM);
        for (int i = 0; i < R_NUM; i++) {
            rows.add(new ArrayList<>());
        }
    }

    /**
     * Gets the card at the specified coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
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
    public void resetPlayerCards(final int playerTurn, final Hero hero) {
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
     * @param x1 the x coordinate of the attacking card
     * @param y1 the y coordinate of the attacking card
     * @param x2 the x coordinate of the target card
     * @param y2 the y coordinate of the target card
     * @return an error message if the attack is invalid, null otherwise
     */
    public String attack(final int x1, final int y1, final int x2, final int y2) {
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
        attackCard.setUsedAttack(true);
        return null;
    }

    /**
     * Applies the ability of one card to a target card.
     * @param x1 the x coordinate of the card using the ability
     * @param y1 the y coordinate of the card using the ability
     * @param x2 the x coordinate of the target card
     * @param y2 the y coordinate of the target card
     * @return an error message if the ability is invalid, null otherwise
     */
    public String ability(final int x1, final int y1, final int x2, final int y2) {
        Card attackCard = getCard(x1, y1);
        Card targetCard = getCard(x2, y2);
        if (attackCard.isFrozen()) {
            return "Attacker card is frozen.";
        }
        if (attackCard.isUsedAttack()) {
            return "Attacker card has already attacked this turn.";
        }
        if (attackCard.getName().equals("Disciple")) {
            if (x1 / 2 != x2 / 2) {
                return "Attacked card does not belong to the current player.";
            }
            targetCard.setHp(targetCard.getHp() + 2);
        } else if (Arrays.asList(ATTACK_ABILITIES).contains(attackCard.getName())) {
            if (x1 / 2 == x2 / 2) {
                return "Attacked card does not belong to the enemy.";
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
            if (attackCard.getName().equals("The Ripper")) {
                if (targetCard.getAttack() > 2) {
                    targetCard.setAttack(targetCard.getAttack() - 2);
                } else {
                    targetCard.setAttack(0);
                }
            } else if (attackCard.getName().equals("Miraj")) {
                int aux = attackCard.getHp();
                attackCard.setHp(targetCard.getHp());
                targetCard.setHp(aux);
            } else if (attackCard.getName().equals("The Cursed One")) {
                if (targetCard.getAttack() == 0) {
                    rows.get(x2).remove(y2);
                } else {
                    int aux = targetCard.getAttack();
                    targetCard.setAttack(targetCard.getHp());
                    targetCard.setHp(aux);
                }
            }
        }
        attackCard.setUsedAttack(true);
        return null;
    }

    /**
     * Makes the attack of one card against the enemy hero.
     * @param x the x coordinate of the attacking card
     * @param y the y coordinate of the attacking card
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
        if (hero.getName().equals("Lord Royce")
                || hero.getName().equals("Empress Thorina")) {
            if ((playerTurn == 1 && targetRow / 2 == 1)
                    || (playerTurn == 2 && targetRow / 2 == 0)) {
                return "Selected row does not belong to the enemy.";
            }
            if (hero.getName().equals("Lord Royce")) {
                for (Card card : rows.get(targetRow)) {
                    card.setFrozen(true);
                }
            } else if (hero.getName().equals("Empress Thorina")) {
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
            }
        } else if (hero.getName().equals("General Kocioraw")
                || hero.getName().equals("King Mudface")) {
            if ((playerTurn == 1 && targetRow / 2 == 0)
                    || (playerTurn == 2 && targetRow / 2 == 1)) {
                return "Selected row does not belong to the current player.";
            }
            if (hero.getName().equals("General Kocioraw")) {
                for (Card card : rows.get(targetRow)) {
                    card.setAttack(card.getAttack() + 1);
                }
            } else if (hero.getName().equals("King Mudface")) {
                for (Card card : rows.get(targetRow)) {
                    card.setHp(card.getHp() + 1);
                }
            }
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
