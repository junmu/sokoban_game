import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class SokobanGame {
    private Map<Stage, Boolean> stageClearedStatus;
    private List<Stage> stageList;

    public SokobanGame(List<Stage> stageList) {
        if (stageList.size() < 1) throw new IllegalArgumentException("올바르지 않은 스테이지 리스트 입니다.");

        this.stageClearedStatus = new HashMap<>();
        this.stageList = new ArrayList<>();

        for (Stage stage : stageList) {
            stageClearedStatus.put(stage, false);
        }
        this.stageList.addAll(stageList);
    }

    public void startGame(Stage stage) throws Exception {
        Play play = new Play(stage);
        play.start();
        stageClearedStatus.replace(stage, play.isSuccess());
    }

    public void startFirstStage() throws Exception {
        startGame(stageList.get(0));
    }
}