package Onitama.view;

import Onitama.Board;
import Onitama.Figure;
import com.codingame.game.Player;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.SpriteAnimation;
import com.codingame.gameengine.module.tooltip.TooltipModule;

public class FigureView {
    static String[] spritesK0;
    static String[] spritesK1;
    static String[] spritesW0;
    static String[] spritesW1;

    private Figure figure;
    private GraphicEntityModule graphics;
    private TooltipModule tooltips;
    private SpriteAnimation sprite;

    private String[] getSprites(int line) {
        String[] sheet = spritesK0;
        if (figure.getOwner().getIndex() == 0 && figure.isMaster()) sheet = spritesW0;
        if (figure.getOwner().getIndex() == 1 && !figure.isMaster()) sheet = spritesK1;
        if (figure.getOwner().getIndex() == 1 && figure.isMaster()) sheet = spritesW1;

        String[] result = new String[6];
        for (int i = 0; i < 5; i++) result[i] = sheet[5 * line + i];
        result[5] = line == 5? result[4] : sheet[0];
        return result;
    }

    public FigureView(Figure figure, Group boardGroup, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule) {
        this.figure = figure;
        figure.setView(this);
        this.graphics = graphicEntityModule;
        this.tooltips = tooltipModule;

        if (spritesK0 == null) {
            spritesK0 = graphics.createSpriteSheetSplitter().setSourceImage("k0.png").setHeight(1624 / 7).setWidth(1585 / 5).setImageCount(35).setImagesPerRow(5).setOrigRow(0).setOrigCol(0).setName("a").split();
            spritesK1 = graphics.createSpriteSheetSplitter().setSourceImage("k1.png").setHeight(1624 / 7).setWidth(1585 / 5).setImageCount(35).setImagesPerRow(5).setOrigRow(0).setOrigCol(0).setName("b").split();
            spritesW0 = graphics.createSpriteSheetSplitter().setSourceImage("w0.png").setHeight(1666 / 7).setWidth(1630 / 5).setImageCount(35).setImagesPerRow(5).setOrigRow(0).setOrigCol(0).setName("c").split();
            spritesW1 = graphics.createSpriteSheetSplitter().setSourceImage("w1.png").setHeight(1666 / 7).setWidth(1630 / 5).setImageCount(35).setImagesPerRow(5).setOrigRow(0).setOrigCol(0).setName("d").split();
        }
        sprite = graphics.createSpriteAnimation().
                setImages(getSprites(0)).setScale(0.7).setX(figure.getCell().getX() * 150).setY((Board.SIZE - 1 - figure.getCell().getY()) * 150)
                .setLoop(true).play();
        boardGroup.add(sprite);
    }

    public void kill() {
        sprite.reset().setImages(getSprites(5)).setLoop(false).play();
        graphics.commitEntityState(0, sprite);
        sprite.setAlpha(0);
    }

    public void move(boolean attack) {
        sprite.reset().setImages(getSprites(attack ? 4 : 2)).setLoop(false).play();
        graphics.commitEntityState(0, sprite);
        sprite.setX(figure.getCell().getX() * 150).setY((Board.SIZE - 1 - figure.getCell().getY()) * 150);
        sprite.reset().setImages(getSprites(0)).setLoop(true).play();
    }
}
