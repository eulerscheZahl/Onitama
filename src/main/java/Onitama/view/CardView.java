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

    private void drawRect(int dx, int dy, boolean move) {
        Rectangle rect = graphics.createRectangle().setWidth(15).setHeight(15).setX(143 + 16 * dx).setY(43 - 16 * dy);
        if (!move) rect.setFillColor(0x222222);
        else validMoves.add(rect);
        group.add(rect);
    }

    public CardView(Card card, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule, int x, int y, boolean rotated) {
        this.card = card;
        card.setView(this);
        this.graphics = graphicEntityModule;
        this.tooltips = tooltipModule;
        this.x = x - 100;
        this.y = y - 50;
        this.rotated = rotated;

        group = graphicEntityModule.createGroup();
        group.add(graphics.createSprite().setImage("card.png"));
        group.add(graphics.createText(String.valueOf(card.getCardId())).setX(10).setY(10).setFontSize(70));
        drawRect(0,0,false);
        for (int i = 0; i < card.getxMove().size(); i++) {
            int dx = card.getxMove().get(i);
            int dy = card.getyMove().get(i);
            drawRect(dx,dy,true);
        }
        setLocation(false, true);
    }

    private void setLocation(boolean rotate, boolean colorize) {
        if (rotate) rotated = !rotated;
        if (colorize) {
            for (Rectangle rectangle : validMoves) {
                rectangle.setFillColor(Player.getColor(rotated ? 1 : 0));
            }
        }
        group.setX(x + (rotated ? 200 : 0)).setY(y + (rotated ? 100 : 0)).setRotation(rotated ? Math.PI : 0);
    }

    public void swap(CardView toTake) {
        int thisX = this.x;
        int thisY = this.y;
        this.x = toTake.x;
        this.y = toTake.y;
        toTake.x = thisX;
        toTake.y = thisY;
        this.setLocation(true, true);
        toTake.setLocation(false, false);
    }
}
