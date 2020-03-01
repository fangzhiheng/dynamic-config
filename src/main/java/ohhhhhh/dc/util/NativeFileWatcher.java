package ohhhhhh.dc.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author fzh
 * @since 1.0
 */
public class NativeFileWatcher extends AbstractFileWatcher {

    private final static Set<String> EMPTY = Collections.emptySet();

    private final Map<WatchKey, Path> monitoredPaths = new ConcurrentHashMap<>();

    private final Map<Path, Set<String>> monitoredFiles = new ConcurrentHashMap<>();

    private final WatchService watchService;

    public NativeFileWatcher() throws IOException {
        super();
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    @Override
    public void start() {
        super.start();
        getWatcher().execute(() -> {
            while (WatcherStatus.RUNNING.equals(getStatus())) {
                try {
                    WatchKey take = watchService.take();
                    Path dir = monitoredPaths.get(take);
                    Set<String> registeredFiles = monitoredFiles.get(dir);
                    if (!EMPTY.equals(registeredFiles)) {
                        Set<String> effectedFiles = new HashSet<>();
                        if (take.isValid()) {
                            List<WatchEvent<?>> events = take.pollEvents();
                            for (WatchEvent<?> event : events) {
                                String filename = event.context().toString();
                                effectedFiles.add(filename);
                            }
                        }
                        effectedFiles.stream()
                                .filter(registeredFiles::contains)
                                .map(dir::resolve)
                                .forEach(this::notifyUpdate);
                    } else {
                        take.pollEvents();
                        notifyUpdate(dir);
                    }
                    take.reset();
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    @Override
    public void watch(Path path) throws Exception {
        String filename = null;
        if (!Files.isDirectory(path)) {
            filename = path.getFileName().toString();
            path = path.getParent();
            if (!Files.isDirectory(path)) {
                throw new IllegalStateException("invalid path " + path.toString());
            }
        }
        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        if (!monitoredPaths.containsKey(key)) {
            monitoredPaths.put(key, path);
        }
        if (filename != null) {
            Set<String> files = monitoredFiles.get(path);
            if (files == null) {
                files = new ConcurrentSkipListSet<>();
                monitoredFiles.put(path, files);
            }
            if (EMPTY != files) {
                files.add(filename);
            }
        } else {
            monitoredFiles.put(path, EMPTY);
        }
    }

}
