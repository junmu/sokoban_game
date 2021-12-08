import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayStatusStore {
    public static final int MIN_SLOT = 1;
    public static final int MAX_SLOT = 5;

    private final List<PlayStatus> playStatusStore;

    public PlayStatusStore() {
        int size = MAX_SLOT - MIN_SLOT + 1;
        playStatusStore = new ArrayList<>(size);
    }

    public boolean saveStatus(PlayStatus status, int index) {
        if (index >= MIN_SLOT && index <= MAX_SLOT) {
            playStatusStore.add(index, status);
            return true;
        }
        return false;
    }

    public Optional<PlayStatus> loadStatus(int index) {
        return playStatusStore.stream()
                .filter(status -> playStatusStore.indexOf(status) == index)
                .findAny();
    }

    public boolean isAvailableSlot(int index) {
        return (index >= MIN_SLOT && index <= MAX_SLOT);
    }
}
