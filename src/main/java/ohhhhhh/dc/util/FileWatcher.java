package ohhhhhh.dc.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * @author fzh
 * @since 1.0
 */
public interface FileWatcher {

    void notifyUpdate(Path path);

    void onUpdate(Consumer<Path> consumer);

    void watch(Path path) throws Exception;

    WatcherStatus getStatus();

    void start();

    void stop();

    static FileWatcher getFileWatcher() {
        FileWatcher watcher;
        try {
            watcher = new NativeFileWatcher();
        } catch (IOException e) {
            watcher = new PollingFileWatcher(1000);
        }
        return watcher;
    }

    enum WatcherStatus {
        STOPPED, RUNNING
    }

}
