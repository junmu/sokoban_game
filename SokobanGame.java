import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class SokobanGame {
    private Map<Stage, Boolean> stageClearedStatus;
    private List<Stage> stageList;
    private StageWriter writer;

    public SokobanGame(List<Stage> stageList) {
        if (stageList.size() < 1) throw new IllegalArgumentException("올바르지 않은 스테이지 리스트 입니다.");

        this.stageClearedStatus = new HashMap<>();
        this.stageList = new ArrayList<>();

        for (Stage stage : stageList) {
            stageClearedStatus.put(stage, false);
        }
        this.stageList.addAll(stageList);

        writer = new CmdStageWriter();
    }
}
