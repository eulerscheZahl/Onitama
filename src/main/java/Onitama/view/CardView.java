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
    private Group group; // rotate this group - can't set rotation center
    private Group innerGroup; // move this group
    private ArrayList<Rectangle> validMoves = new ArrayList<>();

    private static final int RECT_SIZE = 20;
    private static final int GRID_X = 130;
    private static final int GRID_Y = 20;

    private void drawGrid() {
        for (int i = 0; i <= 5; i++) {
            innerGroup.add(graphics.createLine().setX(GRID_X + RECT_SIZE * i).setY(GRID_Y).setX2(GRID_X + RECT_SIZE * i).setY2(GRID_Y + 5 * RECT_SIZE).setLineWidth(1));
            innerGroup.add(graphics.createLine().setY(GRID_Y + RECT_SIZE * i).setX(GRID_X).setY2(GRID_Y + RECT_SIZE * i).setX2(GRID_X + 5 * RECT_SIZE).setLineWidth(1));
        }
    }

    private void drawRect(int dx, int dy, boolean move) {
        Rectangle rect = graphics.createRectangle().setWidth(RECT_SIZE).setHeight(RECT_SIZE).setX(GRID_X + RECT_SIZE * (dx + 2)).setY(GRID_Y - RECT_SIZE * (dy - 2));
        if (!move) rect.setFillColor(0x222222);
        else validMoves.add(rect);
        innerGroup.add(rect);
    }

    public CardView(Card card, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule, int x, int y, boolean rotated) {
        this.card = card;
        card.setView(this);
        this.graphics = graphicEntityModule;
        this.tooltips = tooltipModule;
        this.x = x - 141;
        this.y = y - 73;
        this.rotated = rotated;


        group = graphicEntityModule.createGroup().setZIndex(1);
        //group.add(graphics.createCircle().setRadius(2).setFillColor(0xff8080));
        innerGroup = graphicEntityModule.createGroup();
        group.add(innerGroup);
        innerGroup.add(graphics.createSprite().setImage("scroll.png"));
        innerGroup.add(graphics.createText(String.valueOf(card.getCardId())).setX(75).setY(50).setAnchor(0.5).setFontSize(70));
        drawRect(0, 0, false);
        for (int i = 0; i < card.getxMove().size(); i++) {
            int dx = card.getxMove().get(i);
            int dy = card.getyMove().get(i);
            drawRect(dx, dy, true);
        }
        drawGrid();
        setLocation(false, y != 1080 / 2, -1, 0, false);
    }

    private void setLocation(boolean rotate, boolean colorize, int playedMove, double epsilon, boolean shiftedRotation) {
        if (rotate) rotated = !rotated;
        int color = colorize ? Player.getColor(rotated ? 1 : 0) : 0x808080;
        for (int i = 0; i < validMoves.size(); i++) {
            Rectangle rectangle = validMoves.get(i);
            if (i == playedMove || playedMove == -1) graphics.commitEntityState(0.5, rectangle);
            rectangle.setFillColor(color);
            if (i != playedMove && playedMove != -1) graphics.commitEntityState(0.2, rectangle);
        }

        int targetX =x + (rotated ^ shiftedRotation ? 283 : 0);
        int targetY = y + (rotated ? 146 : 0);
        if (shiftedRotation) {
            group.setX((group.getX() + targetX) / 2).setY((group.getY() + targetY) / 2).setRotation(rotated ? Math.PI + epsilon : epsilon);
            graphics.commitEntityState(0.75, group);
        }
        group.setX(targetX).setY(targetY).setRotation(rotated ? Math.PI : 0);
    }

    public void swap(CardView toTake, int playedMove) {
        double epsilon = 0; // for rotation direction
        boolean shiftedRotation = false;
        int offset = this.y > toTake.y ? 283 : -283;
        if (this.x < toTake.x == this.y > toTake.y) {
            epsilon = (this.y > toTake.y ? 1 : -1) * Math.PI / 4;
            shiftedRotation = true;
            innerGroup.setX(-283);
            group.setX(group.getX() + offset);
            graphics.commitEntityState(0, group, innerGroup);
        }

        toTake.group.setZIndex(0);
        this.group.setZIndex(2);
        graphics.commitEntityState(0, toTake.group);
        int thisX = this.x;
        int thisY = this.y;
        this.x = toTake.x;
        this.y = toTake.y;
        toTake.x = thisX;
        toTake.y = thisY;
        graphics.commitEntityState(0.5, this.group, toTake.group);
        this.setLocation(true, false, playedMove, epsilon, shiftedRotation);
        toTake.setLocation(false, true, -1, 0, false);

        if (shiftedRotation) {
            graphics.commitEntityState(0.9999, innerGroup, group);
            innerGroup.setX(0);
            group.setX(group.getX() + offset);
        }
        graphics.commitEntityState(1, this.group, toTake.group);
        toTake.group.setZIndex(1);
        this.group.setZIndex(1);
    }
}
