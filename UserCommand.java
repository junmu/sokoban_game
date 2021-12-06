import java.util.Arrays;

public enum UserCommand {
    W, // 위
    A, // 왼쪽
    S, // 아래
    D, // 오른쪽
    Q; // 프로그램 종료

    public static boolean isValidCommand(String inputCommand) {
        return Arrays.stream(UserCommand.values())
                .anyMatch(userCommand -> userCommand.name().equals(inputCommand.toUpperCase()));
    }
}
