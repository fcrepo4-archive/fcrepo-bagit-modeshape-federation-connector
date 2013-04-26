
package org.fcrepo.federation.bagit;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.List;

public class ManifestMonitor implements Runnable {

    private final BagItConnector connector;

    private final BagItWatchService watchService;

    private volatile boolean shutdown;

    public ManifestMonitor(final BagItConnector connector)
            throws IOException {
        this.connector = connector;
        this.watchService =
                new BagItWatchService(connector.getBagItDirectory());
        this.shutdown = false;
    }

    @Override
    public void run() {
        while (!this.shutdown) {
            try {
                final WatchKey key = watchService.poll(1, SECONDS);
                if (key != null) {
                    final List<WatchEvent<?>> events = key.pollEvents();
                    boolean manifest = false;
                    boolean tagManifest = false;
                    Path path = null;
                    for (final WatchEvent<?> event : events) {
                        final Path context = (Path) event.context();
                        if (watchService.isManifest(context)) {
                            manifest = true;
                            path = context;
                        } else if (watchService.isTagManifest(context)) {
                            tagManifest = true;
                            path = context;
                        }
                    }
                    if (manifest) {
                        connector.changeManifest(path.toFile());
                    } else if (tagManifest) {
                        connector.changeTagFile(path.toFile());
                    }
                }
            } catch (final InterruptedException e) {
                this.shutdown = true;
            }
        }
    }

    public void shutdown() {
        this.shutdown = true;
    }

}
