import java.util.List;

public class Main {
    public static void main(String[] args) {
        boolean printAll = false;
        if (args.length > 0 && args[0].equals("PRINT_ERROR")) printAll = true;

        try {
            StageReader reader = new CmdStageReader();
            List<Stage> gameMapList = reader.readAllGameMaps();
            StringBuilder sb = new StringBuilder();

            for (Stage map : gameMapList) {
                printAllData(map);
            }

        } catch (Exception e) {
            if (printAll) e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("예기치 않게 종료되었습니다.");
        }
    }

    private static void printAllData(Stage stage) {
        System.out.printf("Stage %d\n\n", stage.getStageIndex());
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
