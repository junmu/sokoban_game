package app.sokoban.io;

import app.sokoban.play.Stage;

import java.io.OutputStreamWriter;
import java.util.List;

public class CmdStageWriter implements StageWriter {

    private OutputStreamWriter writer;

    public CmdStageWriter() {
        writer = new OutputStreamWriter(System.out);
    }

    @Override
    public void writeStage(char[][] chrMap) throws Exception {
        for(char[] arr : chrMap) {
            for (char chr : arr) {
                writer.append(chr);
            }
            writer.append("\n");
        }
        writer.append("\n")
                .flush();
    }

    @Override
    public void writeStage(Stage stage) throws Exception {
        writer.append("app.sokoban.play.Stage " + stage.getStageIndex())
                .append("\n")
                .append(stage.chrMapToString())
                .flush();
    }

    @Override
    public void writeAllStages(List<Stage> stageList) throws Exception {
        for (Stage stage : stageList) {
            writeStage(stage);
            writer.write("\n");
        }
    }

    @Override
    public void close() throws Exception {
        writer = null;
    }

    @Override
    public boolean isClosed() {
        return writer == null;
    }
}
