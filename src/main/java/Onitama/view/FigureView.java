package Onitama.view;

import Onitama.Board;
import Onitama.Cell;
import Onitama.Figure;
import com.codingame.game.Player;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.SpriteAnimation;
import com.codingame.gameengine.module.tooltip.TooltipModule;

import java.util.HashMap;

public class FigureView {
    private Figure figure;
    private Cell oldPosition;
    private GraphicEntityModule graphics;
    private TooltipModule tooltips;
    private SpriteAnimation sprite;
    private MultiplayerGameManager<Player> gameManager;

    private static HashMap<String, String[]> spritesheets = new HashMap<>();
    private static int knightHeight = 232;
    private static int wizardHeight = 238;
    private static int[] knightWidth = {210, 220, 230, 240, 270, 315, 265};
    private static int[] wizardWidth = {200, 200, 230, 200, 340, 200, 260};
    private static String[] states = {"IDLE", "WALK", "RUN", "JUMP", "ATTACK", "DIE", "HURT"};

    public FigureView(Figure figure, MultiplayerGameManager<Player> gameManager, Group boardGroup, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule) {
        this.figure = figure;
        oldPosition = figure.getCell();
        figure.setView(this);
        this.gameManager = gameManager;
        this.graphics = graphicEntityModule;
        this.tooltips = tooltipModule;

        sprite = graphics.createSpriteAnimation()
                .setImages(getSprites("IDLE")).setScale(0.7)
                .setX(figure.getCell().getX() * 150).setY((Board.SIZE - 1 - figure.getCell().getY()) * 150)
                .setLoop(false).setPlaying(false);
        boardGroup.add(sprite);
        tooltips.setTooltipText(sprite, getTooltipText());
    }

    private String[] getSprites(String state) {
        String base = figure.isMaster() ? "w" : "k";
        base += figure.getOwner().getIndex();
        base += state.charAt(0);
        if (!spritesheets.containsKey(base)) {
            int stateIndex = 0;
            while (!state.equals(states[stateIndex])) stateIndex++;
            int width = figure.isMaster() ? wizardWidth[stateIndex] : knightWidth[stateIndex];
            int height = figure.isMaster() ? wizardHeight : knightHeight;
            String[] sprites = graphics.createSpriteSheetSplitter()
                    .setSourceImage(base + ".png")
                    .setImageCount(5)
                    .setWidth(width)
                    .setHeight(height)
                    .setOrigRow(0)
                    .setOrigCol(0)
                    .setImagesPerRow(5)
                    .setName(base)
                    .split();
            spritesheets.put(base, sprites);
        }

        String[] result = new String[5];
        for (int i = 0; i < 5; i++) result[i] = spritesheets.get(base)[i];
        return result;
    }

    private String getTooltipText() {
        String result = figure.isMaster() ? "Master" : "Student";
        result += "\nowner: " + figure.getOwner().getIndex();
        result += "\ncell: " + figure.getCell().printCoord();
        return result;
    }

    public void kill(Player killer) {
        if (!figure.isMaster() && sprite.getScaleX() > 0) sprite.setX(sprite.getX() - 60);
        sprite.setZIndex(1).reset().setImages(getSprites("DIE")).setLoop(false).play();
        graphics.commitEntityState(0, sprite);
        graphics.commitEntityState(0.8, sprite);
        sprite.setAlpha(0);
        tooltips.setTooltipText(sprite, "");
        if (figure.isMaster())
            gameManager.addTooltip(killer, String.format("%s captured the opponent master and won the game", killer.getNicknameToken()));
        else gameManager.addTooltip(killer, String.format("%s captured a student", killer.getNicknameToken()));
    }

    private void setSpritePosition(Cell cell) {
        sprite.setY((Board.SIZE - 1 - cell.getY()) * 150);
        int spriteX = cell.getX() * 150;
        if (sprite.getScaleX() > 0) sprite.setAnchorX(0).setX(spriteX);
        else sprite.setAnchorX(1).setX(spriteX);
    }

    public void move(boolean attack) {
        double scale = Math.abs(sprite.getScaleX());
        if (oldPosition.getX() > figure.getCell().getX()) sprite.setScaleX(-scale);
        else if (oldPosition.getX() < figure.getCell().getX()) sprite.setScaleX(scale);
        sprite.setZIndex(2).reset().setImages(getSprites(attack ? "ATTACK" : "RUN")).play();
        setSpritePosition(oldPosition);
        sprite.setAlpha(0.9999); // hacky workaround for SDK bug, won't play animation otherwise
        graphics.commitEntityState(0, sprite);
        setSpritePosition(figure.getCell());
        sprite.setAlpha(1);
        tooltips.setTooltipText(sprite, getTooltipText());
        graphics.commitEntityState(0.999, sprite);
        sprite.setZIndex(0).reset().setImages(getSprites("IDLE")).setPlaying(false);
        setSpritePosition(figure.getCell());
        oldPosition = figure.getCell();
    }
}
