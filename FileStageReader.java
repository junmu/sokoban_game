import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class FileStageReader implements StageReader {

    private File file;
    private Scanner sc;

    public FileStageReader(String filePath) throws FileNotFoundException {
        file = new File(filePath);

        if (!file.exists()) throw new IllegalArgumentException("파일의 경로가 잘못되었습니다.[" + filePath + "]");

        sc = new Scanner(file);
    }

    @Override
    public Optional<Stage> readStage() {
        List<String> lines = new ArrayList<>();
        boolean isStartOfStage = false;
        int stageIndex = 0;

        while(!isClosed()) {
            String line = sc.nextLine();

            if (StageUtils.isEndOfStage(line)) break; // 스테이지 끝 부분이면 종료
            if (!isStartOfStage && StageUtils.isStartOfStage(line)) { // 스테이지 시작 부분인지 확인
                isStartOfStage = true;
                stageIndex = StageUtils.convertToStageIndex(line);
                continue;
            }
            if (isStartOfStage) lines.add(line); // 입력에서 지도 추출
        }

        if (!sc.hasNext()) close(); // 입력이 종료되었으면 종료
        return Optional.of(new Stage(lines, stageIndex));
    }

    @Override
    public List<Stage> readAllStages() {
        List<Stage> stageList = new ArrayList<>();

        while(!isClosed()) {
            readStage().ifPresent(stageList::add);
        }

        return stageList;
    }

    @Override
    public void close() {
        sc.close();
        sc = null;
    }

    @Override
    public boolean isClosed() {
        return sc == null;
    }
}
