package Onitama;

import com.codingame.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    public static final int SIZE = 5;
    private Cell[][] grid = new Cell[SIZE][SIZE];
    private Card middleCard;
    private List<List<Card>> playerCards = new ArrayList<>();

    public Board(List<Player> players, Random random) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grid[x][y] = new Cell(x, y);
            }
        }

        new Figure(grid[SIZE / 2][0], players.get(0), true);
        new Figure(grid[SIZE / 2][SIZE - 1], players.get(1), true);
        for (int x = 0; x < SIZE; x++) {
            if (grid[x][0].getFigure() == null) {
                new Figure(grid[x][0], players.get(0), false);
                new Figure(grid[x][SIZE - 1], players.get(1), false);
            }
        }

        List<Card> cardSet = new ArrayList<>();
        cardSet.add(new Card(new int[]{-1, -1, 1}, new int[]{1, -1, 0})); // EEL
        cardSet.add(new Card(new int[]{-1, 0, 1}, new int[]{-1, 1, -1})); // CRANE
        cardSet.add(new Card(new int[]{-2, -1, 1}, new int[]{0, 1, -1})); // FROG
        cardSet.add(new Card(new int[]{-1, 0, 0}, new int[]{0, 1, -1})); // HORSE
        cardSet.add(new Card(new int[]{-1, 0, 1}, new int[]{0, 1, 0})); // BOAR
        cardSet.add(new Card(new int[]{-1, -1, 1, 1}, new int[]{-1, 0, 0, 1})); // ROOSTER
        cardSet.add(new Card(new int[]{-2, 0, 2}, new int[]{0, 1, 0})); // CRAB
        cardSet.add(new Card(new int[]{0, 0, 1}, new int[]{1, -1, 0})); // OX
        cardSet.add(new Card(new int[]{-1, 1, 1}, new int[]{0, 1, -1})); // COBRA
        cardSet.add(new Card(new int[]{-1, 1, 2}, new int[]{-1, 1, 0})); // RABBIT
        cardSet.add(new Card(new int[]{-2, -1, 1, 2}, new int[]{1, -1, -1, 1})); // DRAGON
        cardSet.add(new Card(new int[]{-1, -1, 1, 1}, new int[]{1, -1, 1, -1})); // MONKEY
        cardSet.add(new Card(new int[]{-1, -1, 1, 1}, new int[]{1, 0, 0, -1})); // GOOSE
        cardSet.add(new Card(new int[]{-1, -1, 1, 1}, new int[]{1, 0, 1, 0})); // ELEPHANT
        cardSet.add(new Card(new int[]{-1, 0, 1}, new int[]{1, -1, 1})); // MANTIS
        cardSet.add(new Card(new int[]{0, 0}, new int[]{-1, 2})); // TIGER
        while (cardSet.size() > 5) {
            cardSet.remove(random.nextInt(cardSet.size()));
        }

        playerCards.add(new ArrayList<>());
        playerCards.get(0).add((cardSet.get(0)));
        playerCards.get(0).add((cardSet.get(1)));
        playerCards.add(new ArrayList<>());
        playerCards.get(1).add((cardSet.get(2)));
        playerCards.get(1).add((cardSet.get(3)));
        middleCard = cardSet.get(4);
    }

    public ArrayList<String> printState() {
        ArrayList<String> result = new ArrayList<>();
        for (int y = SIZE - 1; y >= 0; y--) {
            String line = "";
            for (int x = 0; x < SIZE; x++) {
                line += grid[x][y].toString();
            }
            result.add(line);
        }
        for (Card card : playerCards.get(0)) result.add("0 " + card.toString());
        for (Card card : playerCards.get(1)) result.add("1 " + card.toString());
        result.add("-1 " + middleCard.toString());
        return result;
    }

    public boolean inGrid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    public void play(Player player, int cardId, String move) throws Exception {
        Card playedCard = null;
        for (Card card : playerCards.get(player.getIndex())) {
            if (card.getCardId() == cardId) playedCard = card;
        }
        if (playedCard == null)
            throw new Exception("Player " + player.getIndex() + " does not own a card with ID " + cardId);

        if (!move.equals("PASS")) {
            int yFrom = move.charAt(0) - 'A';
            int xFrom = move.charAt(1) - '1';
            int yTo = move.charAt(2) - 'A';
            int xTo = move.charAt(3) - '1';
            if (!inGrid(xFrom, yFrom)) throw new Exception("Source field isn't on the grid");
            if (!inGrid(xTo, yTo)) throw new Exception("Destination field isn't on the grid");
            int dx = xTo - xFrom;
            int dy = yTo - yFrom;
            if (player.getIndex() == 1) {
                dx = -dx;
                dy = -dy;
            }
            boolean validAction = dx == 0 && dy == 0;
            for (int i = 0; i < playedCard.getxMove().size(); i++) {
                if (playedCard.getxMove().get(i) == dx && playedCard.getyMove().get(i) == dy) validAction = true;
            }
            if (!validAction) throw new Exception("Card " + cardId + " does not support the move " + move);
            if (grid[xFrom][yFrom].getFigure() == null) throw new Exception("There is no figure on the source cell");
            if (grid[xFrom][yFrom].getFigure().getOwner() != player)
                throw new Exception("The figure on the source cell does not belong to the player");
            if (grid[xTo][yTo].getFigure() != null && grid[xTo][yTo].getFigure().getOwner() == player)
                throw new Exception("You can't capture your own figures");
            grid[xFrom][yFrom].getFigure().moveTo(grid[xTo][yTo]);
        }

        playerCards.get(player.getIndex()).remove(playedCard);
        playerCards.get(player.getIndex()).add(middleCard);
        playedCard.playCard(middleCard);
        middleCard = playedCard;
    }

    public Card getCard(int player, int number) {
        if (player == -1) return middleCard;
        return playerCards.get(player).get(number);
    }

    public boolean hasWinner(List<Player> players) {
        Figure[] masters = new Figure[2];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Figure figure = grid[x][y].getFigure();
                if (figure != null && figure.isMaster()) masters[figure.getOwner().getIndex()] = figure;
            }
        }

        if (masters[0] == null || masters[0].getCell().getX() == SIZE / 2 && masters[0].getCell().getY() == SIZE - 1) {
            players.get(1).setScore(1);
            return true;
        }
        if (masters[1] == null || masters[1].getCell().getX() == SIZE / 2 && masters[1].getCell().getY() == 0) {
            players.get(0).setScore(1);
            return true;
        }

        return false;
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }
}
