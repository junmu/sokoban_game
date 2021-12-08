import java.util.Optional;

public class PlayStatusStore {
    public static final int MIN_SLOT = 1;
    public static final int MAX_SLOT = 5;

    private final PlayStatus[] playStatusStore;

    public PlayStatusStore() {
        int size = MAX_SLOT - MIN_SLOT + 1;
        playStatusStore = new PlayStatus[size];
    }

    public boolean saveStatus(PlayStatus status, int index) {
        if (index >= MIN_SLOT && index <= MAX_SLOT) {
            playStatusStore[index-1] = new PlayStatus.PlayStatusBuilder()
                    .setStage(status.getStage())
                    .setPlayer(new Position(status.getPlayer()))
                    .setPlayingMap(status.getClonePlayingMap())
                    .setPlayerMoveCount(status.getPlayerMoveCount())
                    .setSuccess(status.isSuccess())
                    .setQuit(status.isQuit())
                    .build();
            return true;
        }
        return false;
    }

    public Optional<PlayStatus> loadStatus(int index) {
        return Optional.of(playStatusStore[index-1]);
    }

    public boolean isAvailableSlot(int index) {
        return (index >= MIN_SLOT && index <= MAX_SLOT);
    }
}
