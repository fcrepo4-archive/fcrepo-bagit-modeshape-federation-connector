
package org.fcrepo.federation.bagit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.util.List;

import org.modeshape.jcr.federation.spi.change.ConnectorChangedSet;
import org.slf4j.Logger;

public class ManifestMonitor implements Runnable {

    private final BagItConnector connector;

    private final BagItWatchService watchService;

    private volatile boolean shutdown;

    private static final Logger logger = getLogger(ManifestMonitor.class);

    public ManifestMonitor(final BagItConnector connector)
            throws IOException {
        logger.debug(
                "Initializing ManifestMonitor on BagItConnector on directory: {}",
                connector.getBagItDirectory());
        this.connector = connector;
        this.watchService =
                new BagItWatchService(connector.getBagItDirectory());
        this.shutdown = false;
    }

    @Override
    public void run() {
        logger.debug("Now executing ManifestMonitor.run()...");
        while (!this.shutdown) {
            try {
                final WatchKey key = watchService.poll(10, SECONDS);
                if (key != null) {
                    final List<WatchEvent<?>> events = key.pollEvents();
                    final Path path = null;
                    for (final WatchEvent<?> event : events) {
                        final Path context = (Path) event.context();
                        final Kind kind = event.kind();
                        logger.debug(
                                "Received an event at context: {} of kind: {}",
                                context.toAbsolutePath(), kind.name());
                        final ConnectorChangedSet changes =
                                connector.newConnectorChangedSet();
                        if (watchService.isManifest(context)) {

                            final Boolean manifest = true;
                        } else if (watchService.isTagManifest(context)) {
                            final Boolean tagManifest = true;
                        }
                    }

                }
            } catch (final InterruptedException e) {
                logger.debug("Now ManifestMonitor.run() interrupted.");
                this.shutdown = true;
            }
        }
    }

    public void shutdown() {
        logger.debug(
                "Shutting down ManifestMonitor on BagItConnector on directory: {}",
                connector.getBagItDirectory());
        this.shutdown = true;
    }

}
