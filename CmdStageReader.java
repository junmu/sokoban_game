import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CmdStageReader implements StageReader {

    private Scanner sc;

    public CmdStageReader() {
        sc = new Scanner(System.in);
    }

    @Override
    public Stage readStage() {
        List<String> lines = new ArrayList<>();
        boolean isStartOfStage = false;
        int stageIndex = 0;

        while(!isClosed()) {
            String line = sc.nextLine();

            if (StageUtils.isEndOfStage(line)) break; // 스테이지 끝 부분이면 종료
            if (closeReaderIfEndOfInput(line)) break; // 입력이 종료되었으면 종료
            if (!isStartOfStage && StageUtils.isStartOfStage(line)) { // 스테이지 시작 부분인지 확인
                isStartOfStage = true;
                stageIndex = StageUtils.convertToStageIndex(line);
                continue;
            }
            if (isStartOfStage) lines.add(line); // 입력에서 지도 추출
        }

        return new Stage(lines, stageIndex);
    }

    @Override
    public List<Stage> readAllStages() {
        List<Stage> stageList = new ArrayList<>();

        while(!isClosed()) {
            stageList.add(readStage());
        }

        return stageList;
    }

    @Override
    public void close() {
        sc = null;
    }

    @Override
    public boolean isClosed() {
        return (sc == null);
    }

    private boolean closeReaderIfEndOfInput(String line) {
        boolean isEndOfInput = line.isBlank();

        if (isEndOfInput) close();

        return isEndOfInput;
    }
}
