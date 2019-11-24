package Onitama;

public class Cell {
    private int x;
    private int y;
    private Figure figure;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString(){
        if (figure == null) return ".";
        return figure.toString();
    }
}
