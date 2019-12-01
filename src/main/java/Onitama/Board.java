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

        while (middleCard == null) {
            Card.resetId();
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
            for (int i = cardSet.size() - 1; i > 0; i--) {
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

            boolean inBlacklist = false;
            for (int[] start : blacklist) {
                if (middleCard.getCardId() == start[4] &&
                        playerCards.get(0).stream().anyMatch(c -> c.getCardId() == start[0]) &&
                        playerCards.get(0).stream().anyMatch(c -> c.getCardId() == start[1]) &&
                        playerCards.get(1).stream().anyMatch(c -> c.getCardId() == start[2]) &&
                        playerCards.get(1).stream().anyMatch(c -> c.getCardId() == start[3])
                ) {
                    inBlacklist = true;
                }
            }
            if (inBlacklist) {
                playerCards.clear();
                middleCard = null;
            }
        }
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

    // the following starting positions are solvable at depth=6 with player 1 winning the game - exclude these from that map generator
    // w,w,b,b,center
    private static int[][] blacklist = {
            {0,5,2,12,15},
            {0,5,8,9,15},
            {0,8,2,12,15},
            {0,8,5,9,15},
            {0,9,2,12,15},
            {0,9,5,8,15},
            {0,11,2,12,15},
            {0,13,2,12,15},
            {0,14,2,12,15},
            {0,15,2,12,5},
            {0,15,2,12,8},
            {0,15,2,12,9},
            {0,15,2,12,11},
            {0,15,2,12,13},
            {0,15,2,12,14},
            {1,3,4,6,15},
            {1,3,4,7,15},
            {1,3,6,7,15},
            {1,4,3,6,15},
            {1,4,3,7,15},
            {1,4,6,7,15},
            {1,6,3,4,15},
            {1,6,3,7,15},
            {1,6,4,7,15},
            {1,7,3,4,15},
            {1,7,3,6,15},
            {1,7,4,6,15},
            {1,15,3,4,6},
            {1,15,3,4,7},
            {1,15,3,6,4},
            {1,15,3,6,7},
            {1,15,3,7,4},
            {1,15,3,7,6},
            {1,15,4,6,3},
            {1,15,4,6,7},
            {1,15,4,7,3},
            {1,15,4,7,6},
            {1,15,6,7,3},
            {1,15,6,7,4},
            {2,5,0,12,15},
            {2,5,8,9,15},
            {2,8,0,12,15},
            {2,8,5,9,15},
            {2,9,0,12,15},
            {2,9,5,8,15},
            {2,11,0,12,15},
            {2,13,0,12,15},
            {2,14,0,12,15},
            {2,15,0,12,5},
            {2,15,0,12,8},
            {2,15,0,12,9},
            {2,15,0,12,11},
            {2,15,0,12,13},
            {2,15,0,12,14},
            {3,4,1,6,15},
            {3,4,1,7,15},
            {3,4,6,7,15},
            {3,6,1,4,15},
            {3,6,1,7,15},
            {3,6,4,7,15},
            {3,7,1,4,15},
            {3,7,1,6,15},
            {3,7,4,6,15},
            {3,15,1,4,6},
            {3,15,1,4,7},
            {3,15,1,6,4},
            {3,15,1,6,7},
            {3,15,1,7,4},
            {3,15,1,7,6},
            {3,15,4,6,1},
            {3,15,4,6,7},
            {3,15,4,7,1},
            {3,15,4,7,6},
            {3,15,6,7,1},
            {3,15,6,7,4},
            {4,6,1,3,15},
            {4,6,1,7,15},
            {4,6,3,7,15},
            {4,7,1,3,15},
            {4,7,1,6,15},
            {4,7,3,6,15},
            {4,15,1,3,6},
            {4,15,1,3,7},
            {4,15,1,6,3},
            {4,15,1,6,7},
            {4,15,1,7,3},
            {4,15,1,7,6},
            {4,15,3,6,1},
            {4,15,3,6,7},
            {4,15,3,7,1},
            {4,15,3,7,6},
            {4,15,6,7,1},
            {4,15,6,7,3},
            {5,11,8,9,15},
            {5,12,0,2,15},
            {5,12,8,9,15},
            {5,13,8,9,15},
            {5,14,8,9,15},
            {5,15,8,9,0},
            {5,15,8,9,2},
            {5,15,8,9,11},
            {5,15,8,9,12},
            {5,15,8,9,13},
            {5,15,8,9,14},
            {6,7,1,3,15},
            {6,7,1,4,15},
            {6,7,3,4,15},
            {6,15,1,3,4},
            {6,15,1,3,7},
            {6,15,1,4,3},
            {6,15,1,4,7},
            {6,15,1,7,3},
            {6,15,1,7,4},
            {6,15,3,4,1},
            {6,15,3,4,7},
            {6,15,3,7,1},
            {6,15,3,7,4},
            {6,15,4,7,1},
            {6,15,4,7,3},
            {7,15,1,3,4},
            {7,15,1,3,6},
            {7,15,1,4,3},
            {7,15,1,4,6},
            {7,15,1,6,3},
            {7,15,1,6,4},
            {7,15,3,4,1},
            {7,15,3,4,6},
            {7,15,3,6,1},
            {7,15,3,6,4},
            {7,15,4,6,1},
            {7,15,4,6,3},
            {8,11,5,9,15},
            {8,12,0,2,15},
            {8,12,5,9,15},
            {8,13,5,9,15},
            {8,14,5,9,15},
            {8,15,5,9,0},
            {8,15,5,9,2},
            {8,15,5,9,11},
            {8,15,5,9,12},
            {8,15,5,9,13},
            {8,15,5,9,14},
            {9,11,5,8,15},
            {9,12,0,2,15},
            {9,12,5,8,15},
            {9,13,5,8,15},
            {9,14,5,8,15},
            {9,15,5,8,0},
            {9,15,5,8,2},
            {9,15,5,8,11},
            {9,15,5,8,12},
            {9,15,5,8,13},
            {9,15,5,8,14},
            {11,12,0,2,15},
            {12,13,0,2,15},
            {12,14,0,2,15},
            {12,15,0,2,5},
            {12,15,0,2,8},
            {12,15,0,2,9},
            {12,15,0,2,11},
            {12,15,0,2,13},
            {12,15,0,2,14},
    };
}
