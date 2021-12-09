package app.sokoban.play;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
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

    public void starBgm() {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream input = AudioSystem.getAudioInputStream(new File("mattoglseby-4.wav"));
            clip.open(input);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("배경음악을 찾지 못했습니다.");
        }
    }

    public void startFirstStage() {
        starBgm();
        startGame(stageList.get(0));
    }

    public void startGame(Stage stage) {
        Play play = new Play(stage);
        play.start();
        stageClearedStatus.replace(stage, play.isSuccess());

        if (play.isSuccess()) {
            Long count = countOfClearedStages();
            System.out.println("클리어한 스테이지 수 " + count + "/" + stageList.size() + "\n");
            nextStage(stage);
        }
    }

    public void nextStage(Stage stage) {
        findNextStage(stage)
                .ifPresentOrElse(this::startGame, this::endOfGame);
    }

    private Optional<Stage> findNextStage(Stage stage) {
        int index = stageList.indexOf(stage);

        return stageList.stream()
                .filter(stg -> stageList.indexOf(stg) == index + 1)
                .findAny();
    }

    private boolean isAllCleared() {
        return stageClearedStatus.values()
                .stream()
                .filter(isCleared -> isCleared == true)
                .count() == stageList.size();
    }

    private Long countOfClearedStages() {
        return stageClearedStatus.values()
                .stream()
                .filter(isCleared -> isCleared == true)
                .count();
    }

    private void endOfGame() {
        if (!isAllCleared()) {
            System.out.println("마지막 스테이지 입니다.");
            return;
        }

        System.out.println("전체 게임을 클리어하셨습니다!!");

    }
}