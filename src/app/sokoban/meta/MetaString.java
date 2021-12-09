package app.sokoban.meta;

import java.util.Arrays;

public enum MetaString {
    STAGE_START("Stage"),
    STAGE_END("=");

    private String keyWord;

    MetaString(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public static boolean isValidName(String argKeyWord) {
        return Arrays.stream(MetaString.values())
                .anyMatch(metaStr -> metaStr.getKeyWord().equals(argKeyWord));
    }
}
