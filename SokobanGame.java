import java.util.*;

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

    public void startFirstStage() {
        startGame(stageList.get(0));
    }

    public void startGame(Stage stage) {
        Play play = new Play(stage);
        play.start();
        stageClearedStatus.replace(stage, play.isSuccess());

        if (play.isSuccess()) {
            nextStage(stage);
        }
    }

    public void nextStage(Stage stage) {
        findNextStage(stage)
                .ifPresentOrElse(this::startGame, () -> System.out.println("전체 게임을 클리어하셨습니다!!\n"));
    }

    private Optional<Stage> findNextStage(Stage stage) {
        int index = stageList.indexOf(stage);

        return stageList.stream()
                .filter(stg -> stageList.indexOf(stg) == index + 1)
                .findAny();
    }
}