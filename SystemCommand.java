import java.util.Arrays;
import java.util.Optional;

public enum SystemCommand {
    q("프로그램을 종료합니다."),             // 프로그램 종료
    r("스테이지를 초기화 합니다."),          // 스테이지 초기화
    S("세이브 슬롯에 진행상황을 저장합니다."),  // 스테이지 저장
    L("세이브 슬롯에서 진행상황을 불러옵니다."), // 스테이지 로드
    u("한 턴 되돌리기"),                   // 한 턴 되돌리기
    U("되돌리기 취소");                    // 되돌리기 취소

    private String message;

    SystemCommand(String message) {
        this.message = message;
    }

    public static boolean isValidCommand(String inputCommand) {
        return Arrays.stream(SystemCommand.values())
                .anyMatch(systemCommand -> systemCommand.name().equals(inputCommand));
    }

    public static Optional<SystemCommand> findSystemCommand(String inputCommand) {
        return Arrays.stream(SystemCommand.values())
                .filter(systemCommand -> systemCommand.name().equals(inputCommand))
                .findAny();
    }

    public String getMessage() {
        return message;
    }
}
