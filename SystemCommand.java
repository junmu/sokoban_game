import java.util.Arrays;
import java.util.Optional;

public enum SystemCommand {
    Q("프로그램을 종료합니다."),             // 프로그램 종료
    R("스테이지를 초기화 합니다."),          // 스테이지 초기화
    S("세이브 슬롯에 진행상황을 저장합니다."),  // 스테이지 저장
    L("세이브 슬롯에서 진행상황을 불러옵니다."); // 스테이지 로드

    private String message;

    SystemCommand(String message) {
        this.message = message;
    }

    public static boolean isValidCommand(String inputCommand) {
        return Arrays.stream(SystemCommand.values())
                .anyMatch(systemCommand -> systemCommand.name().equalsIgnoreCase(inputCommand));
    }

    public static Optional<SystemCommand> findSystemCommand(String inputCommand) {
        return Arrays.stream(SystemCommand.values())
                .filter(systemCommand -> systemCommand.name().equalsIgnoreCase(inputCommand))
                .findAny();
    }

    public String getMessage() {
        return message;
    }
}
