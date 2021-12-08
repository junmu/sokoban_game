import java.util.Scanner;

public class Play {
    private final Stage stage;
    private Position player;
    private StageWriter writer;
    private char[][] playingMap;
    private int playerMoveCount;
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
        this.success = false;
    }

    private boolean isBall(char chr) {
        return Sign.BALL.getMean() == chr;
    }

    private boolean isPlayer(char chr) {
        return Sign.PLAYER.getMean() == chr;
    }

    private boolean isBallInHall(char chr) {
        return Sign.BALL_IN_HALL.getMean() == chr;
    }

    public boolean isSuccess() {
        return success;
    }

    public void start() {
        try {
            Scanner sc = new Scanner(System.in);
            boolean containsQuit = false;

            System.out.println("Stage: " + stage.getStageIndex());
            writer.writeStage(playingMap);

            while (!containsQuit && !success) {
                char[] commands = getUserCommands(sc);
                containsQuit = executeAllCommands(commands);
            }

            if (containsQuit) System.out.println("Bye~");
            if (success) System.out.println("이동 횟수 : " + playerMoveCount + "\n성공!! 축하합니다.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new IllegalStateException("게임 실행 중 오류가 발생하였습니다.");
        }
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
        for (char cmd : commands) {
            String command = String.valueOf(cmd);

            if (!isValidCommand(command)) {
                printWarning();
                break;
            }
            if (SystemCommand.isValidCommand(command)) {
                boolean containsQuit = executeSystemCommand(command);
                if (containsQuit) return true;
            }
            if (PlayerCommand.isValidCommand(command)) {
                executePlayerCommand(command);
                if (isSuccess()) break;
            }
        }

        return false;
    }

    private boolean isValidCommand(String command) {
        return SystemCommand.isValidCommand(command) || PlayerCommand.isValidCommand(command);
    }

    private boolean executeSystemCommand(String command) throws Exception {
        boolean isQuit = false;

        if (isQuit(command)) {
            isQuit = true;
            System.out.println(SystemCommand.Q.getMessage());
        }

        if (isReset(command)) {
            reset();
        }

        return isQuit;
    }

    private boolean isQuit(String command) {
        return command.equalsIgnoreCase(SystemCommand.Q.name());
    }

    private boolean isReset(String command) {
        return command.equalsIgnoreCase(SystemCommand.R.name());
    }

    private void reset() throws Exception{
        System.out.println(SystemCommand.R.getMessage());
        init();
        writer.writeStage(playingMap);
    }

    private boolean checkSuccess() {
        int ballInHallCount = 0;
        for(char[] arr : playingMap) {
            for(char chr : arr) {
                if (isBallInHall(chr)) ballInHallCount++;
            }
        }

        return ballInHallCount == stage.getBallCount();
    }

    private void executePlayerCommand(String command) {
        System.out.println("\n명령어: " + command);
        PlayerCommand.findPlayerCommand(command)
                .ifPresent(this::moveProcess);
    }

    private void moveProcess(PlayerCommand playerCommand) {
        Point direction = playerCommand.getDirection();
        Point nextPoint = getPlayerNextStep(direction);

        char next = getValueOfPlayingMap(nextPoint);
        boolean isMoveable = true;
        if (isBall(next) || isBallInHall(next)) {
            isMoveable = tryMoveBall(nextPoint, direction);
        }

        if (isMoveable) {
            movePlayer(playerCommand);
        }

        if (checkSuccess()) this.success = true;
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

        moveBallPosition(ball, direction);

        return isBallMoveable;
    }

    private void moveBallPosition(Point ball, Point direction) {
        int nx = ball.getX() + direction.getX();
        int ny = ball.getY() + direction.getY();

        char origin = stage.getOriginValueOfChrMap(ball); // BALL이 있던 위치의 원래 값
        char next = getValueOfPlayingMap(nx, ny);         // BALL이 이동할 위치의 원래 값
        char nextValue = Sign.BALL.getMean();             // BALL이 이동할 위치에 들어갈 값

        if (isBall(origin) || isPlayer(origin)) origin = Sign.EMPTY.getMean();
        if (Sign.HALL.getMean() == next) nextValue = Sign.BALL_IN_HALL.getMean();

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

    private void movePlayer(PlayerCommand playerCommand) {
        try {
            Point direction = playerCommand.getDirection();

            if (!isPlayerMoveable(player, direction)) {
                printWarning();
                return;
            }

            movePlayerPosition(player, direction);

            System.out.println(playerCommand.name() + ": " + playerCommand.getMessage());
            writer.writeStage(playingMap);
        } catch (Exception e) {
            throw new IllegalStateException("플레이어를 움직이는 도중 문제가 발생하였습니다.[" + playerCommand.name() + "]");
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

        if (Sign.PLAYER.getMean() == origin ||
                Sign.BALL.getMean() == origin) origin = Sign.EMPTY.getMean();

        playerMoveCount++;

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