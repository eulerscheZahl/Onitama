package Onitama;

import Onitama.view.FigureView;
import com.codingame.game.Player;

public class Figure {
    private Player owner;
    private boolean master;
    private Cell cell;
    private FigureView view;

    public Figure(Cell cell, Player owner, boolean master) {
        this.cell = cell;
        this.owner = owner;
        this.master = master;
        cell.setFigure(this);
    }

    public boolean isMaster() {
        return master;
    }

    public Player getOwner() {
        return owner;
    }

    public Cell getCell() {
        return cell;
    }

    public void moveTo(Cell to) {
        this.cell.setFigure(null);
        if (to.getFigure() != null) to.getFigure().kill(this.getOwner());
        boolean attack = to.getFigure() != null;
        to.setFigure(this);
        this.cell = to;
        view.move(attack);
    }

    private void kill(Player killer) {
        view.kill(killer);
    }

    @Override
    public String toString() {
        String result = "b";
        if (owner.getIndex() == 0) result = "w";
        if (master) result = result.toUpperCase();
        return result;
    }

    public void setView(FigureView view) {
        this.view = view;
    }
}
