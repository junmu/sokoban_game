package app.sokoban;

import app.sokoban.io.*;
import app.sokoban.play.SokobanGame;
import app.sokoban.play.Stage;

import java.io.File;
import java.util.List;

public class Main {
    private static final String MAP_FILE = "map.txt";
    private static final String ENCRYPTED_MAP_FILE = "map_enc.txt";

    public static void main(String[] args) {
        boolean printError = true;//(args.length > 0 && args[0].equals("PRINT_ERROR"));

        try {
            gameStart();
        } catch (Exception e) {
            if (printError) e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("예기치 않게 종료되었습니다.");
        }
    }

    private static void gameStart() throws Exception {
        String encryptedMap = getCurrentDirectory() + ENCRYPTED_MAP_FILE;

        File file = new File(encryptedMap);
        if (!file.exists()) readAndWriteEncryptedMap(file);

        List<Stage> stageList = readEncryptedStage(file.getAbsolutePath());
        SokobanGame game = new SokobanGame(stageList);
        game.startFirstStage();
    }

    private static void readAndWriteEncryptedMap(File file) throws Exception {
        String map = getCurrentDirectory() + MAP_FILE;

        List<Stage> stageList = readStage(map);
        writeEncryptedStage(file.getAbsolutePath(), stageList);
    }

    private static List<Stage> readStage(String fullPath) throws Exception {
        StageReader reader = new FileStageReader(fullPath);
        return reader.readAllStages();
    }

    private static List<Stage> readEncryptedStage(String fullPath) throws Exception {
        StageReader reader = new AES256FileStageReader(fullPath);
        return reader.readAllStages();
    }

    private static void writeEncryptedStage(String fullPath, List<Stage> stageList) throws Exception {
        StageWriter writer = new AES256FileStageWriter(fullPath);
        writer.writeAllStages(stageList);
        writer.close();
    }

    private static String getCurrentDirectory() {
        return System.getProperty("user.dir") + "/";
    }

    private static void printAllData(List<Stage> stageList) {
        for (Stage stage : stageList) {
            System.out.printf("app.sokoban.play.Stage %d\n\n", stage.getStageIndex());
            System.out.print(stage.chrMapToString());
            System.out.printf("가로 크기 : %d\n", stage.getMapSize().getX());
            System.out.printf("세로 크기 : %d\n", stage.getMapSize().getY());
            System.out.printf("구멍의 수 : %d\n", stage.getHallCount());
            System.out.printf("공의 수 : %d\n", stage.getBallCount());
            int y = stage.getPlayerLocation().getY() + 1;
            int x = stage.getPlayerLocation().getX() + 1;
            System.out.printf("플레이어 위치 (%d, %d)\n\n", y, x);
        }
    }
}
