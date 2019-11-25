import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Agent1 {

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);
        int playerId = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        System.err.println(playerId);
        Random random = new Random(0);

        // game loop
        while (true) {
            ArrayList<String> oppUnits = new ArrayList<>();
            char myChar = playerId == 0 ? 'w' : 'b';
            for (int y = 4; y >= 0; y--) {
                String board = in.nextLine();
                System.err.println(board);
                for (int x = 0; x < board.length(); x++) {
                    if (board.toLowerCase().charAt(x) != myChar && board.charAt(x) != '.')
                        oppUnits.add("" + (char) (x + 'A') + (char) (y + '1'));
                }
            }
            for (int i = 0; i < 5; i++) {
                int owner = in.nextInt();
                int cardId = in.nextInt();
                int dx1 = in.nextInt();
                int dy1 = in.nextInt();
                int dx2 = in.nextInt();
                int dy2 = in.nextInt();
                int dx3 = in.nextInt();
                int dy3 = in.nextInt();
                int dx4 = in.nextInt();
                int dy4 = in.nextInt();
                System.err.println(owner + " " + cardId + " " + dx1 + " " + dy1 + " " + dx2 + " " + dy2 + " " + dx3 + " " + dy3 + " " + dx4 + " " + dy4);
            }
            int actionCount = in.nextInt();
            System.err.println(actionCount);
            ArrayList<String> validActions = new ArrayList<>();
            for (int i = 0; i < actionCount; i++) {
                int cardId = in.nextInt();
                String move = in.next();
                validActions.add(cardId + " " + move);
                System.err.println(cardId + " " + move);
            }
            in.nextLine();

            String action = null;
            boolean kill = false;
            for (String s : validActions) {
                if (oppUnits.stream().anyMatch(opp -> s.endsWith(opp))) {
                    action = s;
                    kill = true;
                }
            }
            if (action == null) action = validActions.get(random.nextInt(validActions.size()));

            System.out.println(action + (kill ? " killing" : " moving") + " the warrior");
        }
    }
}
