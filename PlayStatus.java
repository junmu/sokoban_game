import java.util.Arrays;
import java.util.Stack;

public class PlayStatus {
    private Stage stage;
    private Position player;
    private char[][] playingMap;
    private int playerMoveCount;
    private boolean success;
    private boolean quit;
    private final Stack<String> doStack;
    private final Stack<String> undoStack;

    private PlayStatus(Stage stage,
                       Position player,
                       char[][] playingMap,
                       int playerMoveCount,
                       boolean success,
                       boolean quit,
                       Stack<String> doStack,
                       Stack<String> undoStack) {
        this.stage = stage;
        this.player = player;
        this.playingMap = playingMap;
        this.playerMoveCount = playerMoveCount;
        this.success = success;
        this.quit = quit;
        this.doStack = doStack;
        this.undoStack = undoStack;
    }

    public Stage getStage() {
        return stage;
    }

    public Position getPlayer() {
        return player;
    }

    public int getPlayerMoveCount() {
        return playerMoveCount;
    }

    public void playerMoved() {
        playerMoveCount++;
    }

    public void undoPlayerMoved() {
        playerMoveCount--;
    }

    public char[][] getClonePlayingMap() {
        char[][] cloneMap = new char[playingMap.length][];

        for (int y=0; y<cloneMap.length; y++) {
            cloneMap[y] = Arrays.copyOf(playingMap[y], playingMap[y].length);
        }

        return cloneMap;
    }

    public char getValueOfPlayingMap(Point point) {
        return playingMap[point.getY()][point.getX()];
    }

    public char getValueOfPlayingMap(int x, int y) {
        return playingMap[y][x];
    }

    public void setValueOnPlayingMap(Point point, char value) {
        playingMap[point.getY()][point.getX()] = value;
    }

    public void setValueOnPlayingMap(int x, int y, char value) {
        playingMap[y][x] = value;
    }

    public boolean isOutOfBoundOnPlayingMap(int x, int y) {
        return y < 0 || y >= playingMap.length || x < 0 || x >= playingMap[y].length;
    }

    public void printPlayingMap() throws Exception{
        StageWriter writer = new CmdStageWriter();
        writer.writeStage(playingMap);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isQuit() {
        return quit;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }

    public boolean checkSuccess() {
        int ballInHallCount = 0;
        for(char[] arr : playingMap) {
            for(char chr : arr) {
                if (isBallInHall(chr)) ballInHallCount++;
            }
        }

        return ballInHallCount == stage.getBallCount();
    }

    private boolean isBallInHall(char chr) {
        return Sign.BALL_IN_HALL.getMean() == chr;
    }

    public void pushDoStack(String command) {
        doStack.push(command);
    }

    public String popDoStack() {
        return doStack.pop();
    }

    public boolean isDoStackEmpty() {
        return doStack.isEmpty();
    }

    public void pushUndoStack(String command) {
        undoStack.push(command);
    }

    public String popUndoStack() {
        return undoStack.pop();
    }

    public boolean isUndoStackEmpty() {
        return undoStack.isEmpty();
    }

    public Stack<String> cloneDoStack() {
        return (Stack<String>) doStack.clone();
    }

    public Stack<String> cloneUndoStack() {
        return (Stack<String>) undoStack.clone();
    }

    public static class PlayStatusBuilder {
        private Stage stage;
        private Position player;
        private char[][] playingMap;
        private int playerMoveCount;
        private boolean success;
        private boolean quit;
        public Stack<String> doStack;
        public Stack<String> undoStack;

        public PlayStatusBuilder setStage(Stage stage) {
            this.stage = stage;
            return this;
        }

        public PlayStatusBuilder setPlayer(Position player) {
            this.player = player;
            return this;
        }

        public PlayStatusBuilder setPlayingMap(char[][] playingMap) {
            this.playingMap = playingMap;
            return this;
        }

        public PlayStatusBuilder setPlayerMoveCount(int playerMoveCount) {
            this.playerMoveCount = playerMoveCount;
            return this;
        }

        public PlayStatusBuilder setSuccess(boolean success) {
            this.success = success;
            return this;
        }

        public PlayStatusBuilder setQuit(boolean quit) {
            this.quit = quit;
            return this;
        }

        public PlayStatusBuilder setDoStack(Stack<String> doStack) {
            this.doStack = doStack;
            return this;
        }

        public PlayStatusBuilder setUndoStack(Stack<String> undoStack) {
            this.undoStack = undoStack;
            return this;
        }

        public PlayStatus build() {
            if (this.doStack == null) doStack = new Stack<>();
            if (this.undoStack == null) undoStack = new Stack<>();

            return new PlayStatus(stage,
                                player,
                                playingMap,
                                playerMoveCount,
                                success,
                                quit,
                                doStack,
                                undoStack);
        }
    }
}
