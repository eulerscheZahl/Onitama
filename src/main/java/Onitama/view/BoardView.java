package Onitama.view;

import Onitama.Board;
import Onitama.Figure;
import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Rectangle;
import com.codingame.gameengine.module.tooltip.TooltipModule;

import java.util.List;

public class BoardView {
    private Board board;
    private GraphicEntityModule graphics;
    private TooltipModule tooltips;

    public BoardView(Board board, MultiplayerGameManager<Player> gameManager, GraphicEntityModule graphicEntityModule, TooltipModule tooltipModule, List<Player> players) {
        this.board = board;
        this.graphics = graphicEntityModule;
        this.tooltips = tooltipModule;

        Group boardGroup = graphics.createGroup().setX(200).setY((1080 - 150 * Board.SIZE) / 2);
        boardGroup.add(graphics.createSprite().setImage("board.png").setScale(1.47).setX(-140).setY(-145));
        Rectangle shrine0 = graphicEntityModule.createRectangle().setRotation(Math.PI / 4).setWidth(70).setHeight(70).setX(375).setY(612).setAlpha(0.5).setFillColor(players.get(0).getColor());
        Rectangle shrine1 = graphicEntityModule.createRectangle().setRotation(Math.PI / 4).setWidth(70).setHeight(70).setX(375).setY(12).setAlpha(0.5).setFillColor(players.get(1).getColor());
        boardGroup.add(shrine0, shrine1);
        tooltips.setTooltipText(shrine0, "Shrine\nOwner: 0");
        tooltips.setTooltipText(shrine1, "Shrine\nOwner: 1");

        for (int x = 0; x < board.SIZE; x++) {
            for (int y = 0; y < board.SIZE; y++) {
                Figure figure = board.getCell(x, y).getFigure();
                if (figure != null) new FigureView(figure, gameManager, boardGroup, graphicEntityModule, tooltipModule);
            }
        }

        new CardView(board.getCard(0, 0), graphicEntityModule, tooltipModule, 1350, 680, false);
        new CardView(board.getCard(0, 1), graphicEntityModule, tooltipModule, 1650, 680, false);
        new CardView(board.getCard(1, 0), graphicEntityModule, tooltipModule, 1350, 400, true);
        new CardView(board.getCard(1, 1), graphicEntityModule, tooltipModule, 1650, 400, true);
        new CardView(board.getCard(-1, 0), graphicEntityModule, tooltipModule, 1500, 1080 / 2, false);

        graphicEntityModule.createText(players.get(0).getNicknameToken()).setX(1300).setY(850).setZIndex(1).setFontSize(50).setFillColor(players.get(0).getColor());
        players.get(0).setMessageBox(graphicEntityModule.createText("").setX(1500).setY(970).setZIndex(1).setFontSize(30).setFillColor(0xeeeeee));
        graphicEntityModule.createSprite().setX(1300).setY(970).setImage(players.get(0).getAvatarToken()).setAnchor(0.5).setBaseHeight(110).setBaseWidth(110);

        graphicEntityModule.createText(players.get(1).getNicknameToken()).setX(1300).setY(100).setZIndex(1).setFontSize(50).setFillColor(players.get(1).getColor());
        players.get(1).setMessageBox(graphicEntityModule.createText("").setX(1500).setY(220).setZIndex(1).setFontSize(30).setFillColor(0xeeeeee));
        graphicEntityModule.createSprite().setX(1300).setY(220).setImage(players.get(1).getAvatarToken()).setAnchor(0.5).setBaseHeight(110).setBaseWidth(110);
    }
}
