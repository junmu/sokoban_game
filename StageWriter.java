import java.util.List;

public interface StageWriter {
    void writeStage(char[][] chrMap) throws Exception;
    void writeStage(Stage stage) throws Exception;
    void writeAllStages(List<Stage> stageList) throws Exception;
    void close() throws Exception;
    boolean isClosed();
}
