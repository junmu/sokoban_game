import java.util.List;

public interface StageReader {
    Stage readStage();
    List<Stage> readAllStages();
    void close();
    boolean isClosed();
}
