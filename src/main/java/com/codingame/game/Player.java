package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.module.entities.Text;

public class Player extends AbstractMultiplayerPlayer {
    private Text messageBox;
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

    public void setMessageBox(Text messageBox) {
        this.messageBox = messageBox;
    }

    public void setText(String text) {
        if (text == null) text = "";
        if (messageBox.getText().equals(text)) return;
        messageBox.setText(text);
    }
}
