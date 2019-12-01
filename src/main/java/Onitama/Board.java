package Onitama;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;

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

        // shuffle cards
        for (int i = cardSet.size() - 1; i > 0; i--)
        {
            int index = random.nextInt(i + 1);
            Card tmp = cardSet.get(index);
            cardSet.set(index, cardSet.get(i));
            cardSet.set(i, tmp);
        }

        playerCards.add(new ArrayList<>());
        playerCards.get(0).add((cardSet.get(0)));
        playerCards.get(0).add((cardSet.get(1)));
        playerCards.add(new ArrayList<>());
        playerCards.get(1).add((cardSet.get(2)));
        playerCards.get(1).add((cardSet.get(3)));
        middleCard = cardSet.get(4);
    }

    public ArrayList<String> printState(Player player) {
        ArrayList<String> result = new ArrayList<>();
        for (int y = SIZE - 1; y >= 0; y--) {
            String line = "";
            for (int x = 0; x < SIZE; x++) {
                line += grid[x][y].toString();
            }
            result.add(line);
        }
        for (Card card : playerCards.get(0)) result.add("0 " + card.print(false));
        for (Card card : playerCards.get(1)) result.add("1 " + card.print(true));
        result.add("-1 " + middleCard.print(player.getIndex() == 1));
        return result;
    }

    public static boolean inGrid(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    public ArrayList<Action> generateActions(Player player) {
        ArrayList<Figure> playerFigures = new ArrayList<>();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Figure figure = grid[x][y].getFigure();
                if (figure != null && figure.getOwner() == player) playerFigures.add(figure);
            }
        }

        ArrayList<Action> result = new ArrayList<>();
        for (Card card : playerCards.get(player.getIndex())) {
            for (int move = 0; move < card.getxMove().size(); move++) {
                for (Figure figure : playerFigures) {
                    Action action = new Action(card, figure, move);
                    if (action.isValid(grid)) result.add(action);
                }
            }
        }

        if (result.size() == 0) {
            for (Card card : playerCards.get(player.getIndex())) {
                result.add(new Action(card, null, 0));
            }
        }
        return result;
    }

    public void play(Player player, int cardId, String move) throws Exception {
        if (!playerCards.get(player.getIndex()).stream().anyMatch(c -> c.getCardId() == cardId))
            throw new Exception("Player " + player.getNicknameToken() + " does not own a card with ID " + cardId);

        Action action = null;
        for (Action a : generateActions(player)) {
            if (a.toString().equals(cardId + " " + move)) action = a;
        }
        if (action == null) throw new Exception("Player " + player.getNicknameToken() + " performed an invalid action");

        if (action.getFigure() != null) {
            Cell target = action.getTargetCell();
            target = grid[target.getX()][target.getY()];
            action.getFigure().moveTo(target);
        }

        Card playedCard = action.getCard();
        playerCards.get(player.getIndex()).remove(playedCard);
        playerCards.get(player.getIndex()).add(middleCard);
        playedCard.playCard(middleCard, action.getMove());
        middleCard = playedCard;
    }

    public Card getCard(int player, int number) {
        if (player == -1) return middleCard;
        return playerCards.get(player).get(number);
    }

    public boolean hasWinner(MultiplayerGameManager<Player> gameManager, List<Player> players) {
        Figure[] masters = new Figure[2];
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Figure figure = grid[x][y].getFigure();
                if (figure != null && figure.isMaster()) masters[figure.getOwner().getIndex()] = figure;
            }
        }

        if (masters[0] == null) {
            players.get(1).setScore(1);
            return true;
        }
        if (masters[0].getCell().getX() == SIZE / 2 && masters[0].getCell().getY() == SIZE - 1) {
            players.get(0).setScore(1);
            gameManager.addTooltip(players.get(0), String.format("%s reached the opponent shrine", players.get(0).getNicknameToken()));
            return true;
        }
        if (masters[1] == null) {
            players.get(0).setScore(1);
            return true;
        }
        if (masters[1].getCell().getX() == SIZE / 2 && masters[1].getCell().getY() == 0) {
            players.get(1).setScore(1);
            gameManager.addTooltip(players.get(1), String.format("%s reached the opponent shrine", players.get(1).getNicknameToken()));
            return true;
        }

        return false;
    }

    public Cell getCell(int x, int y) {
        return grid[x][y];
    }
}
