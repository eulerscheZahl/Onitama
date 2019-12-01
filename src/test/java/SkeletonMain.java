import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class SkeletonMain {
    public static void main(String[] args) {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        // Adds as many player as you need to test your game
        gameRunner.addAgent(Agent1.class, "eulerscheZahl", "https://static.codingame.com/servlet/fileservlet?id=29379905825543&format=profile_avatar");
        gameRunner.addAgent(Agent1.class, "CodinGame", "https://static.codingame.com/servlet/fileservlet?id=19333912201092&format=profile_avatar");

        // Another way to add a player
        // gameRunner.addAgent("python3 /home/user/player.py");

        gameRunner.setSeed(-1708481014275134489L);
        gameRunner.start();
    }
}
