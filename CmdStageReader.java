import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CmdStageReader implements StageReader {

    private Scanner sc;

    public CmdStageReader() {
        sc = new Scanner(System.in);
    }

    @Override
    public Stage readGameMap() {
        List<String> lines = new ArrayList<>();
        boolean isStartOfStage = false;
        int stageIndex = 0;

        while(sc.hasNextLine()) {
            String line = sc.nextLine();

            // 스테이지 끝 부분이면 종료
            if (StageUtils.isEndOfStage(line)) break;

            // 입력이 종료되었으면 종료
            if (isEndOfInput(line)) {
                close();
                break;
            }

            // 스테이지 시작 부분인지 확인
            if (!isStartOfStage && StageUtils.isStartOfStage(line)) {
                stageIndex = getStageIndex(line);
                isStartOfStage = true;
                continue;
            }

            // 입력에서 지도 추출
            if (isStartOfStage) lines.add(line);
        }

        return new Stage(lines, stageIndex);
    }

    @Override
    public List<Stage> readAllGameMaps() {
        List<Stage> gameMapList = new ArrayList<>();

        while(!isClosed()) {
            gameMapList.add(readGameMap());
        }

        return gameMapList;
    }

    @Override
    public void close() {
        sc.close();
        sc = null;
    }

    @Override
    public boolean isClosed() {
        return (sc == null);
    }

    private int getStageIndex(String line) {
        int stageIndex;
        try {
            stageIndex = Integer.parseInt(line.split(" ")[1]);
        }
        catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IllegalStateException("스테이지 번호를 읽는데 실패하였습니다.");
        }

        return stageIndex;
    }

    private boolean isEndOfInput(String line) {
        return line.isBlank();
    }
}
