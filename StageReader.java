import java.util.List;

public interface StageReader {
    Stage readGameMap();
    List<Stage> readAllGameMaps();
    void close();
    boolean isClosed();
}
