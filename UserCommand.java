import java.util.Arrays;

public enum UserCommand {
    W(new Point(0, -1)), // 위
    A(new Point(-1, 0)), // 왼쪽
    S(new Point(0, 1)),  // 아래
    D(new Point(1, 0)),  // 오른쪽
    Q(new Point(0, 0));  // 프로그램 종료

    private Point direction;

    UserCommand(Point point) {
        this.direction = point;
    }

    public static boolean isValidCommand(String inputCommand) {
        return Arrays.stream(UserCommand.values())
                .anyMatch(userCommand -> userCommand.name().equals(inputCommand.toUpperCase()));
    }

    public Point getDirection() {
        return direction;
    }
}
