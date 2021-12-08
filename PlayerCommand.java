import java.util.Arrays;
import java.util.Optional;

public enum PlayerCommand {
    W(new Point(0, -1), "위로 이동합니다."),        // 위
    A(new Point(-1, 0), "왼쪽으로 이동합니다."),     // 왼쪽
    S(new Point(0, 1), "아래로 이동합니다."),       // 아래
    D(new Point(1, 0), "오른쪽으로 이동합니다.");    // 오른쪽

    private Point direction;
    private String message;

    PlayerCommand(Point point, String message) {
        this.direction = point;
        this.message = message;
    }

    public static boolean isValidCommand(String inputCommand) {
        return Arrays.stream(PlayerCommand.values())
                .anyMatch(playerCommand -> playerCommand.name().equalsIgnoreCase(inputCommand));
    }

    public static Optional<PlayerCommand> findPlayerCommand(String inputCommand) {
        return Arrays.stream(PlayerCommand.values())
                .filter(playerCommand -> playerCommand.name().equalsIgnoreCase(inputCommand))
                .findAny();
    }

    public Point getDirection() {
        return direction;
    }

    public String getMessage() {
        return message;
    }
}
