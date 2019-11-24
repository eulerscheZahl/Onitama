package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

public class Player extends AbstractMultiplayerPlayer {
    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public static int getColor(int player) {
        if (player == 0) return 0xff8080;
        return 0x8080ff;
    }

    public int getColor() {
        return getColor(getIndex());
    }
}
