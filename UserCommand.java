import java.util.Arrays;
import java.util.Optional;

public enum UserCommand {
    W(new Point(0, -1), "위로 이동합니다."),        // 위
    A(new Point(-1, 0), "왼쪽으로 이동합니다."),     // 왼쪽
    S(new Point(0, 1), "아래로 이동합니다."),       // 아래
    D(new Point(1, 0), "오른쪽으로 이동합니다."),    // 오른쪽
    Q(new Point(0, 0), "프로그램을 종료합니다."),    // 프로그램 종료
    R(new Point(0, 0), "스테이지를 초기화 합니다.");  // 스테이지 초기화

    private Point direction;
    private String message;

    UserCommand(Point point, String message) {
        this.direction = point;
        this.message = message;
    }

    public static boolean isValidCommand(String inputCommand) {
        return Arrays.stream(UserCommand.values())
                .anyMatch(userCommand -> userCommand.name().equalsIgnoreCase(inputCommand));
    }

    public static Optional<UserCommand> findUserCommand(char inputCommand) {
        return Arrays.stream(UserCommand.values())
                .filter(userCommand -> userCommand.name().equalsIgnoreCase(String.valueOf(inputCommand)))
                .findAny();
    }

    public Point getDirection() {
        return direction;
    }

    public String getMessage() {
        return message;
    }
}
