package app.sokoban.play;

import app.sokoban.io.CmdStageWriter;
import app.sokoban.io.StageWriter;
import app.sokoban.meta.Sign;
import app.sokoban.play.command.PlayerCommand;
import app.sokoban.play.command.SystemCommand;
import app.sokoban.play.point.Point;
import app.sokoban.play.point.Position;
import app.sokoban.play.store.PlayStatusStore;

import java.util.Optional;
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

    private Point getNext(Point point, Point direction) {
        int x = point.getX() + direction.getX();
        int y = point.getY() + direction.getY();

        return new Point(x, y);
    }

    public void start() {
        try {
            Scanner sc = new Scanner(System.in);

            writer.writeStage(stage);

            while (!playStatus.isQuit() && !playStatus.isSuccess()) {
                char[] commands = getUserCommands(sc);
                executeAllCommands(commands);
            }

            if (playStatus.isQuit()) System.out.println("Bye~");
            if (playStatus.isSuccess()) System.out.println("이동 횟수 : " + playStatus.getPlayerMoveCount() + "\n성공!! 축하합니다.");

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

    private void executeAllCommands(char[] commands) throws Exception {
        StringBuffer digitBuffer = new StringBuffer();

        for (int i=0; i<commands.length && !playStatus.isQuit(); i++) {
            String command = String.valueOf(commands[i]);

            if(Character.isDigit(commands[i])) {
                digitBuffer.append(commands[i]);
                continue;
            }

            if (SystemCommand.isValidCommand(command)) executeSystemCommand(command, digitBuffer);
            if (playStatus.isQuit()) break;
            if (PlayerCommand.isValidCommand(command)) executePlayerCommand(command);
            if (playStatus.isSuccess()) break;

            if (!isValidCommand(command)) printWarning();
            if (digitBuffer.length() > 0) digitBuffer = new StringBuffer();
        }
    }

    private boolean isValidCommand(String command) {
        return SystemCommand.isValidCommand(command) || PlayerCommand.isValidCommand(command);
    }

    private void executeSystemCommand(String command, StringBuffer digitBuffer) throws Exception {
        if (SystemCommand.isQuit(command)) {
            playStatus.setQuit(true);
            System.out.println(SystemCommand.q.getMessage());
        }

        if (SystemCommand.isReset(command)) {
            reset();
        }

        if (SystemCommand.isSaveOrLoad(command) && digitBuffer.length() > 0) {
            int slotNum = Integer.parseInt(digitBuffer.toString());
            saveOrLoad(command, slotNum);
        }

        if (SystemCommand.isUndoOrCancelUndo(command)) {
            UndoOrCancelUndo(command);
        }
    }

    private void saveOrLoad(String command, int slotNum) throws Exception {
        SystemCommand.findSystemCommand(command).ifPresent((systemCommand) -> {
            System.out.println(slotNum + "번 " + systemCommand.getMessage());

            if (!store.isAvailableSlot(slotNum)) {
                System.out.println("사용할 수 없는 슬롯입니다.");
                return;
            }

            if (SystemCommand.isSave(command) && store.saveStatus(playStatus, slotNum)) System.out.println("저장 성공!");

            if (SystemCommand.isLoad(command)) {
                store.loadStatus(slotNum)
                        .ifPresentOrElse(this::init, () -> System.out.println("진행상황을 불러오지 못하였습니다."));
            }
        });

        playStatus.printPlayingMap();
    }

    private void UndoOrCancelUndo(String command) {
        boolean isStackEmpty = false;

        if (SystemCommand.isUndo(command)) {
            if (!(isStackEmpty = playStatus.isDoStackEmpty())) {
                Turn turn = playStatus.popDoStack();
                undoMoveProcess(turn);
            }
        }

        if (SystemCommand.isCancelUndo(command)) {
            if (!(isStackEmpty = playStatus.isUndoStackEmpty())) {
                Turn turn = playStatus.popUndoStack();
                moveProcess(turn.getCommand());
            }
        }

        if (isStackEmpty) printWarning();
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
        playStatus.clearUndoStack();
    }

    private void moveProcess(PlayerCommand playerCommand) {
        Point playerNextStep = getNext(playStatus.getPlayer(), playerCommand.getDirection());
        char next = playStatus.getValueOfPlayingMap(playerNextStep);
        boolean nextIsBall = (isBall(next) || isBallInHall(next));

        if (nextIsBall) {
            moveBallAndPlayer(playerNextStep, playerCommand);
        } else {
            Optional<Point> movedPlayer = tryMovePlayer(playerCommand);

            if (movedPlayer.isPresent()) {
                Turn turn = new Turn(movedPlayer.get(), playerCommand);

                setMovedTurn(turn);
            }
        }
    }

    private void moveBallAndPlayer(Point playerNextStep, PlayerCommand playerCommand) {
        Optional<Point> movedBall = tryMoveBall(playerNextStep, playerCommand);

        if (movedBall.isPresent()) {
            Optional<Point> movedPlayer = tryMovePlayer(playerCommand);

            if (movedPlayer.isPresent()) {
                Turn turn = new Turn(movedPlayer.get(),movedBall.get(), playerCommand);

                setMovedTurn(turn);
            }
        }
    }

    private void setMovedTurn(Turn turn) {
        playStatus.pushDoStack(turn);
    }

    private Optional<Point> tryMoveBall(Point ball, PlayerCommand playerCommand) {
        Point movedBall;
        Point direction = playerCommand.getDirection();

        if (!isBallMoveable(ball, direction)) {
            printWarning();
            return Optional.empty();
        }

        movedBall = moveBallPosition(ball, direction);

        return Optional.of(movedBall);
    }

    private Point moveBallPosition(Point ball, Point direction) {
        Point movedBall = getNext(ball, direction);

        char origin = stage.getOriginValueOfChrMap(ball);       // BALL이 있던 위치의 원래 값
        char next = playStatus.getValueOfPlayingMap(movedBall); // BALL이 이동할 위치의 원래 값
        char nextValue = Sign.BALL.getMean();                   // BALL이 이동할 위치에 들어갈 값

        if (isBall(origin) || isPlayer(origin)) origin = Sign.EMPTY.getMean();
        if (Sign.HALL.getMean() == next) nextValue = Sign.BALL_IN_HALL.getMean();

        playStatus.setValueOnPlayingMap(ball, origin);
        playStatus.setValueOnPlayingMap(movedBall, nextValue);

        return movedBall;
    }

    private boolean isBallMoveable(Point position, Point direction) {
        Point nextStep = getNext(position, direction);

        if (playStatus.isOutOfBoundOnPlayingMap(nextStep)) return false;

        char next = playStatus.getValueOfPlayingMap(nextStep);

        //EMPTY, HALL 이 아니면 움직일 수 없음
        if (Sign.EMPTY.getMean() != next &&
                Sign.HALL.getMean() != next ) return false;

        return true;
    }

    private Optional<Point> tryMovePlayer(PlayerCommand playerCommand) {
        try {
            Point direction = playerCommand.getDirection();
            if (!isPlayerMoveable(playStatus.getPlayer(), direction)) {
                printWarning();
                return Optional.empty();
            }

            Point movedPlayer = movePlayerPosition(playStatus.getPlayer(), playerCommand.getDirection());
            playStatus.playerMoved();
            if (playStatus.checkSuccess()) playStatus.setSuccess(true);

            System.out.println(playerCommand.name() + ": " + playerCommand.getMessage());
            playStatus.printPlayingMap();

            return Optional.of(movedPlayer);

        } catch (Exception e) {
            throw new IllegalStateException("플레이어를 움직이는 도중 문제가 발생하였습니다.[" + playerCommand.name() + "]");
        }
    }

    private boolean isPlayerMoveable(Position position, Point direction) {
        Point nextStep = getNext(position, direction);

        if (playStatus.isOutOfBoundOnPlayingMap(nextStep)) return false;

        char next = playStatus.getValueOfPlayingMap(nextStep);

        //EMPTY, HALL, BALL_IN_HALL 이 아니면 움직일 수 없음
        if (Sign.EMPTY.getMean() != next &&
                Sign.HALL.getMean() != next &&
                Sign.BALL_IN_HALL.getMean() != next ) return false;

        return true;
    }

    private Point movePlayerPosition(Point player, Point direction) {
        Point nextStep = getNext(player, direction);
        char origin = stage.getOriginValueOfChrMap(player); // PLAYER가 있던 위치의 원래 값

        if (Sign.PLAYER.getMean() == origin ||
                Sign.BALL.getMean() == origin) origin = Sign.EMPTY.getMean();

        playStatus.setValueOnPlayingMap(nextStep, Sign.PLAYER.getMean());
        playStatus.setValueOnPlayingMap(player, origin);

        playStatus.getPlayer().x = nextStep.getX();
        playStatus.getPlayer().y = nextStep.getY();

        return nextStep;
    }

    private void undoMoveProcess(Turn turn) {

        try {
            Point reverse = turn.getCommand().getReverse();
            movePlayerPosition(turn.getMovedPlayer(), reverse);

            if (turn.isBallMoved()) {
                moveBallPosition(turn.getMovedBall(), reverse);
            }

            playStatus.pushUndoStack(turn);

            System.out.println("한 턴 되돌리기: " + turn.getCommand().name());

            playStatus.printPlayingMap();
        } catch (Exception e) {
            throw new IllegalStateException("한 턴 되돌리기 실행 중 문제가 발생하였습니다.[" + turn.getCommand().name() + "]");
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

    public boolean isSuccess() {
        return playStatus.isSuccess();
    }

    public Stage getStage() {
        return stage;
    }
}