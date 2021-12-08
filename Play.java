import java.util.Scanner;

public class Play {
    private Stage stage;
    private StageWriter writer;
    private PlayStatus playStatus;
    private PlayStatusStore store;

    public Play(Stage stage) {
        this.stage = stage;
        writer = new CmdStageWriter();
        store = new PlayStatusStore();
        init();
    }

    private void init() {
        playStatus = new PlayStatus.PlayStatusBuilder()
                .setStage(stage)
                .setPlayer(new Position(stage.getPlayerLocation()))
                .setPlayingMap(stage.getCloneChrMap())
                .setPlayerMoveCount(0)
                .setSuccess(false)
                .setQuit(false)
                .build();
    }

    private void init(PlayStatus status) {
        playStatus = new PlayStatus.PlayStatusBuilder()
                .setStage(status.getStage())
                .setPlayer(status.getPlayer())
                .setPlayingMap(status.getClonePlayingMap())
                .setPlayerMoveCount(status.getPlayerMoveCount())
                .setSuccess(status.isSuccess())
                .setQuit(status.isQuit())
                .build();
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

    public void start() {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.println("Stage: " + playStatus.getStage().getStageIndex());
            writer.writeStage(stage);

            while (!playStatus.isQuit() && !playStatus.isSuccess()) {
                char[] commands = getUserCommands(sc);
                executeAllCommands(commands);
            }

            if (playStatus.isQuit()) System.out.println("Bye~");
            if (playStatus.isSuccess()) System.out.println("이동 횟수 : " + playStatus.getPlayerMoveCount() + "\n성공!! 축하합니다.");

        } catch (Exception e) {
            e.printStackTrace();
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

    private void executeAllCommands(char[] commands) throws Exception {
        StringBuffer digitBuffer = new StringBuffer();

        for (int i=0; i<commands.length && !playStatus.isQuit(); i++) {
            String command = String.valueOf(commands[i]);

            if(Character.isDigit(commands[i])) {
                digitBuffer.append(commands[i]);
                continue;
            }

            if (!isValidCommand(command)) printWarning();

            if (SystemCommand.isValidCommand(command)) executeSystemCommand(command, digitBuffer);
            if (playStatus.isQuit()) break;

            if (PlayerCommand.isValidCommand(command)) executePlayerCommand(command);
            if (playStatus.isSuccess()) break;

            if (digitBuffer.length() > 0) digitBuffer = new StringBuffer();
        }
    }

    private boolean isValidCommand(String command) {
        return SystemCommand.isValidCommand(command) || PlayerCommand.isValidCommand(command);
    }

    private void executeSystemCommand(String command, StringBuffer digitBuffer) throws Exception {
        if (isQuit(command)) {
            playStatus.setQuit(true);
            System.out.println(SystemCommand.q.getMessage());
        }

        if (isReset(command)) {
            reset();
        }

        if (isSaveOrLoad(command, digitBuffer)) {
            int slotNum = Integer.parseInt(digitBuffer.toString());
            saveOrLoad(command, slotNum);
        }
    }

    private void saveOrLoad(String command, int slotNum) throws Exception {
        SystemCommand.findSystemCommand(command).ifPresent((systemCommand) -> {
            System.out.println(slotNum + "번 " + systemCommand.getMessage());

            if (!store.isAvailableSlot(slotNum)) {
                System.out.println("사용할 수 없는 슬롯입니다.");
                return;
            }

            if (isSave(command) && store.saveStatus(playStatus, slotNum)) System.out.println("저장 성공!");

            if (isLoad(command)) {
                store.loadStatus(slotNum)
                        .ifPresentOrElse(this::init, () -> System.out.println("진행상황을 불러오지 못하였습니다."));
            }
        });

        playStatus.printPlayingMap();
    }

    private boolean isQuit(String command) {
        return command.equalsIgnoreCase(SystemCommand.q.name());
    }

    private boolean isReset(String command) {
        return command.equalsIgnoreCase(SystemCommand.r.name());
    }

    private boolean isSave(String command) {
        return command.equalsIgnoreCase(SystemCommand.S.name());
    }

    private boolean isLoad(String command) {
        return command.equalsIgnoreCase(SystemCommand.L.name());
    }

    private boolean isSaveOrLoad(String command, StringBuffer digitBuffer) {
        return (isSave(command) || isLoad(command)) && digitBuffer.length() > 0;
    }

    private void reset() throws Exception{
        System.out.println(SystemCommand.r.getMessage());
        init();
        playStatus.printPlayingMap();
    }

    private void executePlayerCommand(String command) {
        System.out.println("\n명령어: " + command);
        PlayerCommand.findPlayerCommand(command)
                .ifPresent(this::moveProcess);
    }

    private void moveProcess(PlayerCommand playerCommand) {
        Point direction = playerCommand.getDirection();
        Point nextPoint = getPlayerNextStep(direction);

        char next = playStatus.getValueOfPlayingMap(nextPoint);
        boolean isMoveable = true;
        if (isBall(next) || isBallInHall(next)) {
            isMoveable = tryMoveBall(nextPoint, direction);
        }

        if (isMoveable) {
            movePlayer(playerCommand);
        }

        if (playStatus.checkSuccess()) playStatus.setSuccess(true);
    }

    private Point getPlayerNextStep(Point direction) {
        Position player = playStatus.getPlayer();
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
        char next = playStatus.getValueOfPlayingMap(nx, ny);         // BALL이 이동할 위치의 원래 값
        char nextValue = Sign.BALL.getMean();             // BALL이 이동할 위치에 들어갈 값

        if (isBall(origin) || isPlayer(origin)) origin = Sign.EMPTY.getMean();
        if (Sign.HALL.getMean() == next) nextValue = Sign.BALL_IN_HALL.getMean();

        playStatus.setValueOnPlayingMap(ball, origin);
        playStatus.setValueOnPlayingMap(nx, ny, nextValue);
    }

    private boolean isBallMoveable(Point position, Point direction) {
        int x = position.getX() + direction.getX();
        int y = position.getY() + direction.getY();

        if (playStatus.isOutOfBoundOnPlayingMap(x, y)) return false;

        char next = playStatus.getValueOfPlayingMap(x,y);

        //EMPTY, HALL 이 아니면 움직일 수 없음
        if (Sign.EMPTY.getMean() != next &&
                Sign.HALL.getMean() != next ) return false;

        return true;
    }

    private void movePlayer(PlayerCommand playerCommand) {
        try {
            Position player = playStatus.getPlayer();
            Point direction = playerCommand.getDirection();

            if (!isPlayerMoveable(player, direction)) {
                printWarning();
                return;
            }

            movePlayerPosition(player, direction);

            System.out.println(playerCommand.name() + ": " + playerCommand.getMessage());
            playStatus.printPlayingMap();
        } catch (Exception e) {
            throw new IllegalStateException("플레이어를 움직이는 도중 문제가 발생하였습니다.[" + playerCommand.name() + "]");
        }
    }

    private void printWarning() {
        try {
            System.out.println("(경고!) 해당 명령을 수행할 수 없습니다!!");
            playStatus.printPlayingMap();
        } catch (Exception e) {
            throw new IllegalStateException("경고 메세지 출력 중 문제가 발생하였습니다.");
        }
    }

    private boolean isPlayerMoveable(Position position, Point direction) {
        int x = position.x + direction.getX();
        int y = position.y + direction.getY();

        if (playStatus.isOutOfBoundOnPlayingMap(x, y)) return false;

        char next = playStatus.getValueOfPlayingMap(x,y);

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

        playStatus.playerMoved();

        playStatus.setValueOnPlayingMap(nx, ny, Sign.PLAYER.getMean());
        playStatus.setValueOnPlayingMap(player, origin);

        player.x = nx;
        player.y = ny;
    }

    public boolean isSuccess() {
        return playStatus.isSuccess();
    }

    public Stage getStage() {
        return stage;
    }
}