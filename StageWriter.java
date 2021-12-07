import java.util.List;

public interface StageWriter {
    void writeGameMap(char[][] chrMap) throws Exception;
    void writeGameMap(Stage stage) throws Exception;
    void writeAllGameMaps(List<Stage> stageList) throws Exception;
    void close() throws Exception;
    boolean isClosed();
}
