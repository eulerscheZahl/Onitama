import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class Player {

    public static void main(String args[]) {
        class Unit {
            public int x, y;

            public Unit(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        class Action {
            public int id, x, y;

            public Action(int id, int x, int y) {
                this.id = id;
                this.x = x;
                this.y = y;
            }
        }

        Scanner in = new Scanner(System.in);
        int playerId = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        System.err.println(playerId);
        Random random = new Random(0);

        // game loop
        while (true) {
            ArrayList<Unit> myUnits = new ArrayList<>();
            char myChar = playerId == 0 ? 'w' : 'b';
            for (int y = 4; y >= 0; y--) {
                String board = in.nextLine();
                System.err.println(board);
                for (int x = 0; x < board.length(); x++) {
                    if (board.toLowerCase().charAt(x) == myChar) myUnits.add(new Unit(x, y));
                }
            }
            ArrayList<Action> actions = new ArrayList<>();
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
                if (owner != playerId) continue;
                int factor = playerId == 0 ? 1 : -1;
                if (dx1 != 0 || dy1 != 0) actions.add(new Action(cardId, dx1 * factor, dy1 * factor));
                if (dx2 != 0 || dy2 != 0) actions.add(new Action(cardId, dx2 * factor, dy2 * factor));
                if (dx3 != 0 || dy3 != 0) actions.add(new Action(cardId, dx3 * factor, dy3 * factor));
                if (dx4 != 0 || dy4 != 0) actions.add(new Action(cardId, dx4 * factor, dy4 * factor));
            }
            in.nextLine();

            ArrayList<String> moves = new ArrayList<>();
            moves.add(actions.get(0).id + " PASS");
            moves.add(actions.get(actions.size() - 1).id + " PASS");
            for (Unit unit : myUnits) {
                for (Action action : actions) {
                    int targetX = unit.x + action.x;
                    int targetY = unit.y + action.y;
                    if (targetX < 0 || targetX >= 5 || targetY < 0 || targetY >= 5) continue;
                    boolean occupied = false;
                    for (Unit my : myUnits) {
                        if (my.x == targetX && my.y == targetY) occupied = true;
                    }
                    if (!occupied)
                        moves.add(action.id + " " + (char) (unit.x + 'A') + (char) (unit.y + '1') + (char) (targetX + 'A') + (char) (targetY + '1'));
                }
            }

            System.out.println(moves.get(random.nextInt(moves.size())) + " moving the warrior");
        }
    }
}
