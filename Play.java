import java.util.Scanner;

public class Play {
    private final Stage stage;
    private Position player;
    private StageWriter writer;
    private char[][] playingMap;
    private int playerMoveCount;
    private int ballInHallCount;
    private boolean success;

    public Play(Stage stage) {
        this.stage = stage;
        init();
    }

    private void init() {
        this.player = new Position(stage.getPlayerLocation());
        this.writer = new CmdStageWriter();
        this.playingMap = stage.getCloneChrMap();
        this.playerMoveCount = 0;
        this.ballInHallCount = 0;
        this.success = false;
    }

    private boolean isBall(char chr) {
        return Sign.BALL.getMean() == chr;
    }

    private boolean isBallInHall(char chr) {
        return Sign.BALL_IN_HALL.getMean() == chr;
    }

    public boolean isSuccess() {
        return success;
    }

    public void start() throws Exception {
        Scanner sc = new Scanner(System.in);
        boolean containsQuit = false;

        writer.writeStage(playingMap);

        while (!containsQuit && !success) {
            char[] commands = getUserCommands(sc);
            containsQuit = executeAllCommands(commands);
        }

        if (success) {
            System.out.println("성공!! 축하합니다.");
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

    private boolean executeAllCommands(char[] commands) throws Exception {
        boolean containsQuit = false;

        for (char command : commands) {
            containsQuit = isQuit(command);
            if (containsQuit) {
                System.out.println(UserCommand.Q.getMessage());
                break;
            }

            if (isReset(command)) {
                reset();
                continue;
            }

            executeCommand(command);

            if (stage.getBallCount() == ballInHallCount) success = true;

            if (isSuccess()) break;
        }

        return containsQuit;
    }

    private boolean isQuit(char command) {
        return String.valueOf(command).equalsIgnoreCase(UserCommand.Q.name());
    }

    private boolean isReset(char command) {
        return String.valueOf(command).equalsIgnoreCase(UserCommand.R.name());
    }

    private void reset() throws Exception{
        System.out.println(UserCommand.R.getMessage());
        init();
        writer.writeStage(playingMap);
    }

    private void executeCommand(char command) {
        System.out.println("\n명령어: " + command);
        UserCommand.findUserCommand(command)
                .ifPresentOrElse(this::moveProcess, this::printWarning);
    }

    private void moveProcess(UserCommand userCommand) {
        Point direction = userCommand.getDirection();
        Point nextPoint = getPlayerNextStep(direction);

        char next = getValueOfPlayingMap(nextPoint);
        boolean isMoveable = true;
        if (isBall(next) || isBallInHall(next)) {
            isMoveable = tryMoveBall(nextPoint, direction);
        }

        if (isMoveable) {
            movePlayer(userCommand);
        }


    }

    private Point getPlayerNextStep(Point direction) {
        int x = player.x + direction.getX();
        int y = player.y + direction.getY();

        return new Point(x, y);
    }

    private boolean tryMoveBall(Point ball, Point direction) {
        boolean isBallMoveable = isBallMoveable(ball, direction);
        if (!isBallMoveable) {
            printWarning();
            return false;
        }

        movePlayerPosition(ball, direction);

        return isBallMoveable;
    }

    private void movePlayerPosition(Point ball, Point direction) {
        int nx = ball.getX() + direction.getX();
        int ny = ball.getY() + direction.getY();

        char origin = stage.getOriginValueOfChrMap(ball); // BALL이 있던 위치의 원래 값
        char next = getValueOfPlayingMap(nx, ny);         // BALL이 이동할 위치의 원래 값
        char nextValue = Sign.BALL.getMean();             // BALL이 이동할 위치에 들어갈 값

        if (isBall(origin)) origin = Sign.EMPTY.getMean();
        if (Sign.HALL.getMean() == next) nextValue = Sign.BALL_IN_HALL.getMean();

        if (isBallInHall(nextValue)) ballInHallCount++;

        setValueOnPlayingMap(ball, origin);
        setValueOnPlayingMap(nx, ny, nextValue);
    }

    private boolean isBallMoveable(Point position, Point direction) {
        int x = position.getX() + direction.getX();
        int y = position.getY() + direction.getY();

        if (y < 0 || y >= playingMap.length || x < 0 || x >= playingMap[y].length) return false;

        char next = getValueOfPlayingMap(x,y);

        //EMPTY, HALL 이 아니면 움직일 수 없음
        if (Sign.EMPTY.getMean() != next &&
                Sign.HALL.getMean() != next ) return false;

        return true;
    }

    private void movePlayer(UserCommand userCommand) {
        try {
            Point direction = userCommand.getDirection();

            if (!isPlayerMoveable(player, direction)) {
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

    private boolean isPlayerMoveable(Position position, Point direction) {
        int x = position.x + direction.getX();
        int y = position.y + direction.getY();

        if (y < 0 || y >= playingMap.length || x < 0 || x >= playingMap[y].length) return false;

        char next = getValueOfPlayingMap(x,y);

        //EMPTY, HALL, BALL_IN_HALL 이 아니면 움직일 수 없음
        if (Sign.EMPTY.getMean() != next &&
                Sign.HALL.getMean() != next &&
                Sign.BALL_IN_HALL.getMean() != next ) return false;

        return true;
    }

    private void movePlayerPosition(Position player, Point direction) {
        int nx = player.x + direction.getX();
        int ny = player.y + direction.getY();
        char origin = stage.getOriginValueOfChrMap(player); // PLAYER가 있던 위치의 원래 값
        char next = getValueOfPlayingMap(nx, ny);

        if (Sign.PLAYER.getMean() == origin ||
                Sign.BALL.getMean() == origin) origin = Sign.EMPTY.getMean();
        if (isBallInHall(next)) ballInHallCount--;

        setValueOnPlayingMap(nx, ny, Sign.PLAYER.getMean());
        setValueOnPlayingMap(player, origin);

        player.x = nx;
        player.y = ny;
    }

    private char getValueOfPlayingMap(Point point) {
        return playingMap[point.getY()][point.getX()];
    }

    private char getValueOfPlayingMap(int x, int y) {
        return playingMap[y][x];
    }

    private void setValueOnPlayingMap(Point point, char value) {
        playingMap[point.getY()][point.getX()] = value;
    }

    private void setValueOnPlayingMap(int x, int y, char value) {
        playingMap[y][x] = value;
    }
}