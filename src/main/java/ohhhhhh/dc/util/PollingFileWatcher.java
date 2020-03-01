package ohhhhhh.dc.util;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fzh
 * @since 1.0
 */
public class PollingFileWatcher extends AbstractFileWatcher {

    private final Map<Path, Long> paths = new ConcurrentHashMap<>();

    private final long pollInterval;

    public PollingFileWatcher(long pollInterval) {
        this.pollInterval = pollInterval;
    }

    @Override
    public void start() {
        super.start();
        getWatcher().execute(() -> {
            while (WatcherStatus.RUNNING.equals(getStatus())) {
                for (Map.Entry<Path, Long> entry : paths.entrySet()) {
                    Path path = entry.getKey();
                    long current = path.toFile().lastModified();
                    long last = entry.getValue();
                    if (current != last) {
                        entry.setValue(current);
                        notifyUpdate(path);
                    }
                }
                try {
                    Thread.sleep(pollInterval);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    @Override
    public void watch(Path path) {
        paths.put(path, path.toFile().lastModified());
    }

}
