package Onitama.view;

import Onitama.Card;
import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.tooltip.TooltipModule;

import java.util.ArrayList;

public class CardView {
    private Card card;
    private GraphicEntityModule graphics;
    private TooltipModule tooltips;
    private int x;
    private int y;
    private boolean rotated;
    private Group group;
    private ArrayList<Rectangle> validMoves = new ArrayList<>();

    private static final int RECT_SIZE = 20;
    private static final int GRID_X  = 130;
    private static final int GRID_Y  = 20;
    private void drawGrid() {
        for (int i = 0; i <= 5; i++) {
            group.add(graphics.createLine().setX(GRID_X + RECT_SIZE * i).setY(GRID_Y).setX2(GRID_X + RECT_SIZE * i).setY2(GRID_Y + 5 * RECT_SIZE).setLineWidth(1));
            group.add(graphics.createLine().setY(GRID_Y + RECT_SIZE * i).setX(GRID_X).setY2(GRID_Y + RECT_SIZE * i).setX2(GRID_X + 5 * RECT_SIZE).setLineWidth(1));
        }
    }

    private void drawRect(int dx, int dy, boolean move) {
        Rectangle rect = graphics.createRectangle().setWidth(RECT_SIZE).setHeight(RECT_SIZE).setX(GRID_X + RECT_SIZE * (dx + 2)).setY(GRID_Y - RECT_SIZE * (dy - 2));
        if (!move) rect.setFillColor(0x222222);
        else validMoves.add(rect);
        group.add(rect);
    }

    public CardView(Card card, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule, int x, int y, boolean rotated) {
        this.card = card;
        card.setView(this);
        this.graphics = graphicEntityModule;
        this.tooltips = tooltipModule;
        this.x = x - 141;
        this.y = y - 73;
        this.rotated = rotated;

        group = graphicEntityModule.createGroup();
        group.add(graphics.createSprite().setImage("scroll.png"));
        group.add(graphics.createText(String.valueOf(card.getCardId())).setX(75).setY(50).setAnchor(0.5).setFontSize(70));
        drawRect(0, 0, false);
        for (int i = 0; i < card.getxMove().size(); i++) {
            int dx = card.getxMove().get(i);
            int dy = card.getyMove().get(i);
            drawRect(dx, dy, true);
        }
        drawGrid();
        setLocation(false, y != 1080 / 2, -1);
    }

    private void setLocation(boolean rotate, boolean colorize, int playedMove) {
        if (rotate) rotated = !rotated;
        int color = colorize ? Player.getColor(rotated ? 1 : 0) : 0x808080;
        for (int i = 0; i < validMoves.size(); i++) {
            Rectangle rectangle = validMoves.get(i);
            if (i == playedMove || playedMove == -1) graphics.commitEntityState(0.5, rectangle);
            rectangle.setFillColor(color);
            if (i != playedMove && playedMove != -1) graphics.commitEntityState(0.2, rectangle);
        }
        group.setX(x + (rotated ? 283 : 0)).setY(y + (rotated ? 146 : 0)).setRotation(rotated ? Math.PI : 0);
    }

    public void swap(CardView toTake, int playedMove) {
        int thisX = this.x;
        int thisY = this.y;
        this.x = toTake.x;
        this.y = toTake.y;
        toTake.x = thisX;
        toTake.y = thisY;
        graphics.commitEntityState(0.5, this.group, toTake.group);
        this.setLocation(true, false, playedMove);
        toTake.setLocation(false, true, -1);
    }
}
