package app.sokoban.io;

import app.sokoban.encrypt.AES256Encrypter;
import app.sokoban.play.Stage;
import app.sokoban.play.StageUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AES256FileStageReader extends AES256Encrypter implements StageReader {

    private File file;
    BufferedReader reader;

    public AES256FileStageReader(String filePath) throws Exception {
        file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("파일의 경로가 잘못되었습니다.[" + filePath + "]");
        }

        reader = new BufferedReader(new FileReader(file));
    }

    @Override
    public Optional<Stage> readStage() throws Exception {
        List<String> lines = new ArrayList<>();
        boolean stageStarted = false;
        int stageIndex = 0;
        String line;

        while(reader.ready()) {
            line = decrypt(reader.readLine());

            if (StageUtils.isEndOfStage(line)) break; // 스테이지 끝 부분이면 종료
            if (!stageStarted && StageUtils.isStartOfStage(line)) { // 스테이지 시작 부분인지 확인
                stageStarted = true;
                stageIndex = StageUtils.convertToStageIndex(line);
                continue;
            }
            if (stageStarted) lines.add(line); // 입력에서 지도 추출
        }

        return Optional.of(new Stage(lines, stageIndex));
    }

    @Override
    public List<Stage> readAllStages() throws Exception {
        List<Stage> stageList = new ArrayList<>();

        while(!isClosed() && reader.ready()) {
            readStage().ifPresent(stageList::add);
        }

        return stageList;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        reader = null;
    }

    @Override
    public boolean isClosed() {
        return reader == null;
    }
}
