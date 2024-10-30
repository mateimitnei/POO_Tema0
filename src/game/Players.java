package game;
import fileio.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class Players {
    // Getters and setters
    @Getter
    private ArrayList<CardInput> hand1;
    @Getter
    private ArrayList<CardInput> hand2;

    public Players(CardInput card, int index) {
        if (index == 1) {
            hand1 = new ArrayList<>();
            hand1.add(card);
        } else if (index == 2) {
            hand2 = new ArrayList<>();
            hand2.add(card);
        }
    }

    public void drawCard(CardInput card, int index) {
        if (index == 1) {
            hand1.add(card);
        } else if (index == 2) {
            hand2.add(card);
        }
    }
}
