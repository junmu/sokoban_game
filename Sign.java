import java.util.Arrays;

public enum Sign {
    EMPTY(' ', -1),
    WALL('#', 0),
    HALL('O', 1),
    BALL('o', 2),
    PLAYER('P', 3),
    STAGE_DIV('=', 4),
    BALL_IN_HALL('0', 5);

    private char mean;
    private int value;

    Sign(char mean, int value) {
        this.mean = mean;
        this.value = value;
    }

    public char getMean() {
        return mean;
    }

    public int getValue() {
        return value;
    }

    public static boolean isValidMean(char argMean) {
        return Arrays.stream(Sign.values())
                .anyMatch(elem -> elem.getMean() == argMean);
    }

    public static boolean isValidValue(int argValue) {
        return Arrays.stream(Sign.values())
                .anyMatch(elem -> elem.getValue() == argValue);
    }

    public static int convertMeanToValue(char argMean) {
        return Arrays.stream(Sign.values())
                .filter(sign -> sign.getMean() == argMean)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[" + argMean + "]은(는) 선언된 값이 아닙니다."))
                .getValue();
    }

    public static int convertValueToMean(int argValue) {
        return Arrays.stream(Sign.values())
                .filter(sign -> sign.getValue() == argValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("[" + argValue + "]은(는) 선언된 값이 아닙니다."))
                .getMean();
    }
}
