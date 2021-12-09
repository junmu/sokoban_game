import java.util.List;
import java.util.Optional;

public interface StageReader {
    Optional<Stage> readStage() throws Exception;
    List<Stage> readAllStages() throws Exception;
    void close() throws Exception;
    boolean isClosed();
}
