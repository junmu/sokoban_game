public class Turn {
    private Point movedPlayer;
    private Point movedBall;
    private PlayerCommand command;

    public Turn(Point movedPlayer, PlayerCommand command) {
        this.movedPlayer = movedPlayer;
        this.command = command;
    }

    public Turn(Point movedPlayer, Point movedBall, PlayerCommand command) {
        this.movedPlayer = movedPlayer;
        this.movedBall = movedBall;
        this.command = command;
    }

    public Point getMovedPlayer() {
        return movedPlayer;
    }

    public Point getMovedBall() {
        return movedBall;
    }

    public void setMovedBall(Point movedBall) {
        this.movedBall = movedBall;
    }

    public PlayerCommand getCommand() {
        return command;
    }

    public boolean isBallMoved() {
        return movedBall != null;
    }
}
