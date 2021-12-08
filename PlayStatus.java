public class PlayStatus {
    private Stage stage;
    private Position player;
    private char[][] playingMap;
    private int playerMoveCount;
    private boolean success;
    private boolean quit;

    private PlayStatus(Stage stage,
                       Position player,
                       char[][] playingMap,
                       int playerMoveCount,
                       boolean success,
                       boolean quit) {
        this.stage = stage;
        this.player = player;
        this.playingMap = playingMap;
        this.playerMoveCount = playerMoveCount;
        this.success = success;
        this.quit = quit;
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

    public static class PlayStatusBuilder {
        private Stage stage;
        private Position player;
        private char[][] playingMap;
        private int playerMoveCount;
        private boolean success;
        private boolean quit;

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

        public PlayStatus build() {
            return new PlayStatus(stage,
                                player,
                                playingMap,
                                playerMoveCount,
                                success,
                                quit);
        }
    }
}
