import java.util.Arrays;
import java.util.List;

public class Stage {
    private char[][] chrMap;
    private int[][] intMap;
    private int stageIndex;

    private Point mapSize;
    private Point playerLocation;
    private int hallCount;
    private int ballCount;

    public Stage(List<String> lines, int stageIndex) {
        if (lines.size() < 3) {
            throw new IllegalArgumentException("올바르지 않은 맵 정보입니다.");
        }

        // 문자열을 숫자 값으로 변환하여 맵 생성
        this.chrMap = StageUtils.convertToChrMap(lines);
        this.intMap = StageUtils.convertToIntMap(lines);
        this.stageIndex = stageIndex;

        setGameMapData(intMap);
    }

    private void setGameMapData(int[][] argMap) {
        int height = argMap.length;
        int width = argMap[0].length;

        for (int y=0; y<argMap.length; y++) {
            if (width < argMap[y].length) width = argMap[y].length;
            for (int x=0; x<argMap[y].length; x++) {
                int value = argMap[y][x];

                if (!Sign.isValidValue(value)) throw new IllegalArgumentException("맵 정보에 올바르지 않은 문자가 포함되었습니다. [" + value + "]");

                if (Sign.HALL.getValue() == value) hallCount++;
                if (Sign.BALL.getValue() == value) ballCount++;
                if (Sign.PLAYER.getValue() == value) playerLocation = new Point(x, y);
            }
        }

        mapSize = new Point(width, height);
    }

    public char[][] getCloneChrMap() {
        char[][] cloneMap = new char[mapSize.getY()][mapSize.getX()];

        for (int y=0; y<cloneMap.length; y++) {
            cloneMap[y] = Arrays.copyOf(chrMap[y], chrMap[y].length);
        }

        return cloneMap;
    }

    public int[][] getCloneIntMap() {
        int[][] cloneMap = new int[mapSize.getY()][mapSize.getX()];

        for (int y=0; y<cloneMap.length; y++) {
            cloneMap[y] = Arrays.copyOf(intMap[y], intMap[y].length);
        }

        return cloneMap;
    }

    public char getOriginValueOfChrMap(Point point) {
        return chrMap[point.getY()][point.getX()];
    }

    public int getStageIndex() {
        return stageIndex;
    }

    public Point getMapSize() {
        return mapSize;
    }

    public Point getPlayerLocation() {
        return playerLocation;
    }

    public int getHallCount() {
        return hallCount;
    }

    public int getBallCount() {
        return ballCount;
    }

    public String chrMapToString() {
        StringBuilder sb = new StringBuilder();

        for(char[] arr : chrMap) {
            for (char chr : arr) {
                sb.append(chr);
            }
            sb.append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    public String intMapToString() {
        StringBuilder sb = new StringBuilder();

        for(int[] arr : intMap) {
            for (int num : arr) {
                sb.append(num);
            }
            sb.append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }
}