package ohhhhhh.dc.util;

import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author fzh
 * @since 1.0
 */
public abstract class AbstractFileWatcher implements FileWatcher {

    private final Executor watcher;

    private Consumer<Path> consumer;

    private WatcherStatus status;

    protected AbstractFileWatcher() {
        watcher = new ThreadPoolExecutor(1, 1, 1000, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), r -> new Thread(r, getClass().getSimpleName()));
        status = WatcherStatus.STOPPED;
    }

    protected Executor getWatcher() {
        return watcher;
    }

    public void start() {
        this.status = WatcherStatus.RUNNING;
    }

    public void stop() {
        this.status = WatcherStatus.STOPPED;
    }

    @Override
    public void notifyUpdate(Path path) {
        if (this.consumer != null) {
            consumer.accept(path);
        }
    }

    @Override
    public void onUpdate(Consumer<Path> consumer) {
        this.consumer = consumer;
    }

    @Override
    public WatcherStatus getStatus() {
        return status;
    }

}
