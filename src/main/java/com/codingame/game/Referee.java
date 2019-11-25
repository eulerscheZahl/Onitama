package com.codingame.game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Onitama.Action;
import Onitama.Board;
import Onitama.view.BoardView;
import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject
    private MultiplayerGameManager<Player> gameManager;
    @Inject
    private GraphicEntityModule graphicEntityModule;
    @Inject
    private TooltipModule tooltipModule;
    @Inject
    private EndScreenModule endScreenModule;

    static final Pattern PLAYER_PATTERN = Pattern.compile(
            "^(?<cardId>\\d+)\\s+(?<action>[A-E][1-5][A-E][1-5]|PASS)(?:\\s+(?<message>.+))?",
            Pattern.CASE_INSENSITIVE);

    private Board board;

    @Override
    public void init() {
        board = new Board(gameManager.getPlayers(), new Random(gameManager.getSeed()));
        BoardView view = new BoardView(board, gameManager, graphicEntityModule, tooltipModule, gameManager.getPlayers());
    }

    @Override
    public void gameTurn(int turn) {
        turn--;
        Player player = gameManager.getPlayer(turn % 2);
        Player opponent = gameManager.getPlayer((turn+1) % 2);
        if (turn < 2) player.sendInputLine(String.valueOf(player.getIndex()));
        for (String line : board.printState(player)) player.sendInputLine(line);
        ArrayList<Action> moves = board.generateActions(player);
        player.sendInputLine(String.valueOf(moves.size()));
        for (Action action : moves) {
            player.sendInputLine(action.toString());
        }
        player.execute();

        try {
            List<String> outputs = player.getOutputs();
            Matcher match = PLAYER_PATTERN.matcher(outputs.get(0));
            if (match.matches()) {
                int cardId = Integer.parseInt(match.group("cardId"));
                String action = match.group("action").toUpperCase();
                String message = match.group("message");
                board.play(player, cardId, action);
                player.setText(message);
                if (board.hasWinner(gameManager, gameManager.getPlayers())) gameManager.endGame();
            } else throw new Exception(outputs.get(0));
        } catch (TimeoutException e) {
            player.deactivate(String.format("$%d timeout!", player.getIndex()));
            opponent.setScore(1);
            gameManager.endGame();
        } catch (Exception e) {
            player.deactivate(String.format("$%d: invalid command %s", player.getIndex(), e.getMessage()));
            opponent.setScore(1);
            gameManager.endGame();
        }
     }

    @Override
    public void onEnd() {
        int[] scores = gameManager.getPlayers().stream().mapToInt(p -> p.getScore()).toArray();
        String[] texts = new String[2];
        for (int i = 0; i < scores.length; i++) {
            texts[i] = scores[i] == 0 ? "Defeated" : "Winner";
        }
        endScreenModule.setScores(scores, texts);
        String endSprite = "tie";
        if (scores[0] > scores[1]) endSprite = "win0";
        if (scores[0] < scores[1]) endSprite = "win1";
        endScreenModule.setTitleRankingsSprite(endSprite + ".png");
    }
}
