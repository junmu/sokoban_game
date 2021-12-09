import java.io.*;
import java.util.List;

public class AES256FileStageWriter extends AES256Encrypter implements StageWriter {

    private File file;
    private BufferedWriter writer;

    public AES256FileStageWriter(String filePath) throws Exception {
        file = new File(filePath);
        writer = new BufferedWriter(new FileWriter(file));
    }

    @Override
    public void writeStage(char[][] chrMap) throws Exception {
        for(char[] arr : chrMap) {
            StringBuilder sb = new StringBuilder();
            for (char chr : arr) {
                sb.append(chr);
            }
            writer.write(encrypt(sb.toString()) + "\n");
        }
    }

    @Override
    public void writeStage(Stage stage) throws Exception {
        String stageTitle = "Stage " + stage.getStageIndex();
        writer.write(encrypt(stageTitle) + "\n");
        this.writeStage(stage.getCloneChrMap());
    }

    @Override
    public void writeAllStages(List<Stage> stageList) throws Exception {
        String div = String.valueOf(Sign.STAGE_DIV.getMean());

        for (Stage stage : stageList) {
            writeStage(stage);
            if (!isLastStage(stageList, stage)){
                writer.write(encrypt(div) + "\n");
            }
        }
        writer.write(encrypt(div));
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

    private boolean isLastStage(List<Stage> list, Stage stage) {
        return list.indexOf(stage) == list.size() - 1;
    }
}
