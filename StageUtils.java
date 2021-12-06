import java.util.List;

public class StageUtils {
    public static char[][] convertToChrMap(List<String> lines) {
        char[][] map = new char[lines.size()][];

        for (int i=0; i<map.length; i++) {
            map[i] = lines.get(i).toCharArray();
        }

        return map;
    }

    public static int[][] convertToIntMap(List<String> lines) {
        int[][] map = new int[lines.size()][];

        for (int i=0; i<map.length; i++) {
            map[i] = convertToIntRow(lines.get(i));
        }

        return map;
    }

    public static int[] convertToIntRow(String row) {
        char[] chars = row.toCharArray();
        int[] intRow = new int[chars.length];

        for (int i=0; i<chars.length; i++) {
            intRow[i] = Sign.convertMeanToValue(chars[i]);
        }

        return intRow;
    }

    public static boolean isStartOfStage(String line) {
        return line.contains(MetaString.STAGE_START.getKeyWord());
    }

    public static boolean isEndOfStage(String line) {
        return line.contains(MetaString.STAGE_END.getKeyWord());
    }
}
