package Onitama.view;

import Onitama.Board;
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
    private GraphicEntityModule graphics;
    private TooltipModule tooltips;
    private SpriteAnimation sprite;
    private MultiplayerGameManager<Player> gameManager;

    private static HashMap<String, String[]> spritesheets = new HashMap<>();
    private static int knightHeight = 232;
    private static int wizardHeight = 238;
    private static int[] knightWidth = {210, 220, 230, 240, 270, 315, 265};
    private static int[] wizardWidth = {200, 200, 200, 200, 340, 200, 260};
    private static String[] states = {"IDLE", "WALK", "RUN", "JUMP", "ATTACK", "DIE", "HURT"};

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

        String[] result = new String[6];
        for (int i = 0; i < 5; i++) result[i] = spritesheets.get(base)[i];
        result[5] = spritesheets.get(base.substring(0, 2) + "I")[0];
        if (state.charAt(0) == 'D') result[5] = result[4]; // death doesn't end with IDLE
        return result;
    }

    private String getTooltipText() {
        String result = figure.isMaster() ? "Master" : "Student";
        result += "\nowner: " + figure.getOwner().getIndex();
        result += "\ncell: " + figure.getCell().printCoord();
        return result;
    }

    public FigureView(Figure figure, MultiplayerGameManager<Player> gameManager, Group boardGroup, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule) {
        this.figure = figure;
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

    public void kill(Player killer) {
        if (!figure.isMaster()) sprite.setX(sprite.getX() - 60);
        sprite.reset().setImages(getSprites("DIE")).setLoop(false).play();
        graphics.commitEntityState(0, sprite);
        graphics.commitEntityState(0.8, sprite);
        sprite.setAlpha(0);
        tooltips.setTooltipText(sprite, "");
        if (figure.isMaster())
            gameManager.addTooltip(killer, String.format("%s captured the opponent master and won the game", killer.getNicknameToken()));
        else gameManager.addTooltip(killer, String.format("%s captured a student", killer.getNicknameToken()));
    }

    public void move(boolean attack) {
        sprite.reset().setImages(getSprites(attack ? "ATTACK" : "RUN")).play();
        sprite.setAlpha(0.9999); // hacky workaround for SDK bug
        graphics.commitEntityState(0, sprite);
        sprite.setX(figure.getCell().getX() * 150).setY((Board.SIZE - 1 - figure.getCell().getY()) * 150);
        sprite.setAlpha(1);
        tooltips.setTooltipText(sprite, getTooltipText());
        graphics.commitEntityState(0.999, sprite);
        sprite.reset().setImages(getSprites("IDLE")).setPlaying(false);
    }
}
