import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileStageWriter implements StageWriter{

    private File file;
    private BufferedWriter writer;

    public FileStageWriter(String filePath) {
        try {
            file = new File(filePath);
            writer = new BufferedWriter(new FileWriter(file));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeStage(char[][] chrMap) throws Exception {
        for(char[] arr : chrMap) {
            for (char chr : arr) {
                writer.append(chr);
            }
            writer.append("\n");
        }
    }

    @Override
    public void writeStage(Stage stage) throws Exception {
        writer.append("Stage " + stage.getStageIndex() + "\n");
        this.writeStage(stage.getCloneChrMap());
    }

    @Override
    public void writeAllStages(List<Stage> stageList) throws Exception {
        for (Stage stage : stageList) {
            writeStage(stage);
            writer.append(Sign.STAGE_DIV.getMean() + "\n");
        }
    }

    @Override
    public void close() throws Exception {
        writer.close();
        writer = null;
    }

    @Override
    public boolean isClosed() {
        return writer == null;
    }
}
