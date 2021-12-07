import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Play {
    private final Stage stage;
    private Position player;
    private List<Position> balls;
    private StageWriter writer;
    private char[][] playingMap;
    private boolean success;

    public Play(Stage stage) {
        this.stage = stage;
        this.player = new Position(stage.getPlayerLocation());
        this.balls = new ArrayList<>();
        this.writer = new CmdStageWriter();
        this.playingMap = stage.getCloneChrMap();
        this.success = false;

        addAllBallsLocation();
    }

    private void addAllBallsLocation() {
        for (int y=0; y<playingMap.length; y++) {
            for (int x=0; x<playingMap.length; x++) {
                if (isBall(playingMap[y][x])) balls.add(new Position(x, y));
            }
        }
    }

    private boolean isBall(char chr) {
        return Sign.BALL.getMean() == chr;
    }

    public boolean isSuccess() {
        return success;
    }

    public void start() throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean containsQuit = false;

        writer.writeStage(playingMap);

        while (!containsQuit) {
            char[] commands = getUserCommands(sc);
            containsQuit = executeAllCommands(commands);
        }

        System.out.println("Bye~");
    }

    private char[] getUserCommands(Scanner sc) {
        String line = "";

        System.out.print("SOKOBAN> ");
        if (sc.hasNext()) {
            line = sc.next();
        }
        return line.toCharArray();
    }

    private boolean executeAllCommands(char[] commands) {
        boolean containsQuit = false;

        for (char command : commands) {
            containsQuit = isQuit(command);
            if (containsQuit) {
                System.out.println(UserCommand.Q.getMessage());
                break;
            }

            executeCommand(command);
        }

        return containsQuit;
    }

    private boolean isQuit(char command) {
        return String.valueOf(command).equalsIgnoreCase(UserCommand.Q.name());
    }

    private void executeCommand(char command) {
        System.out.println("\n명령어: " + command);
        UserCommand.findUserCommand(command)
                .ifPresentOrElse(this::movePlayer, this::printWarning);
    }

    private void movePlayer(UserCommand userCommand) {
        try {
            Point direction = userCommand.getDirection();

            if (!isMoveable(player, direction)) {
                printWarning();
                return;
            }
            movePlayerPosition(player, direction);
            System.out.println(userCommand.name() + ": " + userCommand.getMessage());
            writer.writeStage(playingMap);
        } catch (Exception e) {
            throw new IllegalStateException("플레이어를 움직이는 도중 문제가 발생하였습니다.[" + userCommand.name() + "]");
        }
    }

    private void printWarning() {
        try {
            System.out.println("(경고!) 해당 명령을 수행할 수 없습니다!!");
            writer.writeStage(playingMap);
        } catch (Exception e) {
            throw new IllegalStateException("경고 메세지 출력 중 문제가 발생하였습니다.");
        }

    }

    private boolean isMoveable(Position position, Point direction) {
        int x = position.x + direction.getX();
        int y = position.y + direction.getY();

        if (y < 0 || y >= playingMap.length || x < 0 || x >= playingMap[y].length) return false;
        if (Sign.EMPTY.getMean() != playingMap[y][x]) return false; //빈 공간이 아니면 움직일 수 없음

        return true;
    }

    private void movePlayerPosition(Position player, Point direction) {
        int nx = player.x + direction.getX();
        int ny = player.y + direction.getY();
        char originSign = stage.getOriginValueOfChrMap(player);

        if (Sign.PLAYER.getMean() == originSign) originSign = Sign.EMPTY.getMean();

        playingMap[ny][nx] = Sign.PLAYER.getMean();
        playingMap[player.y][player.x] = originSign;

        player.x = nx;
        player.y = ny;
    }
}