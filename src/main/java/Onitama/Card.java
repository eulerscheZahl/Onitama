package Onitama;

import Onitama.view.CardView;

import java.util.ArrayList;
import java.util.List;

public class Card {
    private int cardId;
    private List<Integer> xMove = new ArrayList<>();
    private List<Integer> yMove = new ArrayList<>();
    private CardView view;
    private static int cardCounter = 0;

    public Card(int[] xMove, int[] yMove) {
        for (int x : xMove) this.xMove.add(x);
        for (int y : yMove) this.yMove.add(y);
        cardId = cardCounter++;
    }

    public void setView(CardView view) {
        this.view = view;
    }

    public void playCard(Card newCard) {
        this.view.swap(newCard.view);
    }

    public int getCardId() {
        return cardId;
    }

    public List<Integer> getxMove() {
        return xMove;
    }

    public List<Integer> getyMove() {
        return yMove;
    }

    public String print(boolean rotated) {
        int factor = rotated ? -1 : 1;
        String result = String.valueOf(cardId);
        for (int i = 0; i < 4; i++) {
            if (i < xMove.size()) result += " " + factor * xMove.get(i) + " " + factor * yMove.get(i);
            else result += " 0 0";
        }
        return result;
    }
}
