package app.sokoban.play.command;

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

    public boolean equals(String command) {
        return name().equals(command);
    }

    public static boolean isQuit(String command) {
        return q.equals(command);
    }

    public static boolean isReset(String command) {
        return r.equals(command);
    }

    public static boolean isSave(String command) {
        return S.equals(command);
    }

    public static boolean isLoad(String command) {
        return L.equals(command);
    }

    public static boolean isSaveOrLoad(String command) {
        return (isSave(command) || isLoad(command));
    }

    public static boolean isUndo(String command) {
        return u.equals(command);
    }

    public static boolean isCancelUndo(String command) {
        return U.equals(command);
    }

    public static boolean isUndoOrCancelUndo(String command) {
        return (isUndo(command) || isCancelUndo(command));
    }

    public String getMessage() {
        return message;
    }
}
