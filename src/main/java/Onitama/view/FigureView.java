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

public class FigureView {
    private Figure figure;
    private GraphicEntityModule graphics;
    private TooltipModule tooltips;
    private SpriteAnimation sprite;
    private MultiplayerGameManager<Player> gameManager;

    private String[] getSprites(String state) {
        String base = figure.isMaster() ? "W" : "K";
        base += figure.getOwner().getIndex();
        base += state.charAt(0);

        String[] result = new String[6];
        for (int i = 1; i <= 5; i++) result[i - 1] = base + i;
        result[5] = base.substring(0, 2) + "I1";
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

        sprite = graphics.createSpriteAnimation().setAnchorY(1)
                .setImages(getSprites("IDLE")).setScale(0.7).setX(figure.getCell().getX() * 150).setY((Board.SIZE - 1 - figure.getCell().getY()) * 150 + 135)
                .setLoop(true).play();
        boardGroup.add(sprite);
        tooltips.setTooltipText(sprite, getTooltipText());
    }

    public void kill(Player killer) {
        sprite.reset().setImages(getSprites("DIE")).setLoop(false).play();
        graphics.commitEntityState(0, sprite);
        sprite.setAlpha(0);
        tooltips.setTooltipText(sprite, "");
        if (figure.isMaster())
            gameManager.addTooltip(killer, String.format("%s captured the opponent master and won the game", killer.getNicknameToken()));
        else gameManager.addTooltip(killer, String.format("%s captured a student", killer.getNicknameToken()));
    }

    public void move(boolean attack) {
        sprite.reset().setImages(getSprites(attack ? "ATTACK" : "RUN")).setLoop(false).play();
        graphics.commitEntityState(0, sprite);
        sprite.setX(figure.getCell().getX() * 150).setY((Board.SIZE - 1 - figure.getCell().getY()) * 150 + 135);
        sprite.reset().setImages(getSprites("IDLE")).setLoop(true).play();
        tooltips.setTooltipText(sprite, getTooltipText());
    }
}
