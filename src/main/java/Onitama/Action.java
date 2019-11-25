package Onitama;

public class Action {
    private Card card;
    private Figure figure;
    private int move;

    public Action(Card card, Figure figure, int move) {
        this.card = card;
        this.figure = figure;
        this.move = move;
    }

    public Figure getFigure() {
        return figure;
    }

    public boolean isValid(Cell[][] grid) {
        Cell to = getTargetCell();
        if (!Board.inGrid(to.getX(), to.getY())) return false;
        to = grid[to.getX()][to.getY()];
        if (to.getFigure() != null && to.getFigure().getOwner() == figure.getOwner()) return false;
        return true;
    }

    public Cell getTargetCell() {
        int factor = figure.getOwner().getIndex() == 1 ? -1 : 1;
        return new Cell(figure.getCell().getX() + factor * card.getxMove().get(move), figure.getCell().getY() + factor * card.getyMove().get(move));
    }

    @Override
    public String toString() {
        if (figure == null) return card.getCardId() + " PASS";
        return card.getCardId() + " " + figure.getCell().printCoord() + getTargetCell().printCoord();
    }
}
