/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.federation.bagit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.slf4j.Logger;

/**
 * This class monitors a folder containing one or more BagIt folders. It will
 * fire node events via the BagItConnector whenever a manifest is detected or
 * when it or the surrounding BagIt folder are removed.
 * 
 * @author Gregory Jansen, Esme Cowles
 */
public class ManifestMonitor implements Runnable {

    private final BagItConnector connector;

    private WatchService watchService;

    private Path bagItDir = null;

    private volatile boolean shutdown;

    private static final Logger logger = getLogger(ManifestMonitor.class);

    public ManifestMonitor(final BagItConnector connector) throws IOException {
        logger.debug(
                "Initializing ManifestMonitor on BagItConnector on directory: {}",
                connector.getBagItDirectory());
        this.connector = connector;
        this.shutdown = false;
    }

    /**
     * Begins watching a bag directory. Will fire a new bag node event if there
     * is already a manifest or when one is created.
     * 
     * @param path file path to the bag
     */
    private void watchBag(Path path) {
        try {
            path.register(watchService, ENTRY_CREATE, ENTRY_DELETE,
                    ENTRY_MODIFY);
            logger.info("started watching a bag: " + path.toAbsolutePath());
            if (containsManifest(path)) connector.fireNewBagEvent(path);
        } catch (IOException e) {
            logger.warn("Cannot watch bag: " + path.toAbsolutePath(), e);
        }
    }

    private boolean containsManifest(Path path) {
        for (final File bagFile : path.toFile().listFiles()) {
            Path bagPath = Paths.get(bagFile.toURI());
            if (ManifestUtil.isManifest(bagPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        logger.debug("Now executing ManifestMonitor.run()...");
        this.bagItDir = Paths.get(connector.getBagItDirectory().toURI());
        try {
            this.watchService = FileSystems.getDefault().newWatchService();
            this.bagItDir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY,
                    ENTRY_DELETE);
            for (final File file : bagItDir.toFile().listFiles()) {
                if (file.isDirectory()) {
                    Path path = Paths.get(file.toURI());
                    watchBag(path);
                }
            }
        } catch (IOException e) {
            throw new Error(
                    "Cannot set up the monitoring of a BagIt directory: " +
                            this.bagItDir, e);
        }
        while (!this.shutdown) {
            try {
                final WatchKey key = watchService.poll(2, SECONDS);
                if (key != null) {
                    final List<WatchEvent<?>> events = key.pollEvents();
                    Path parent = (Path) key.watchable();
                    for (final WatchEvent<?> event : events) {
                        Path path = (Path) event.context();
                        path = parent.resolve(path);
                        @SuppressWarnings("unchecked")
                        final Kind<Path> kind = (Kind<Path>) event.kind();
                        logger.debug(
                                "Received an event at context: {} of kind: {}",
                                path.toAbsolutePath(), kind.name());
                        if (this.bagItDir.equals(key.watchable())) {
                            if (ENTRY_CREATE == kind) { // new bag
                                watchBag(path);
                            } else if (ENTRY_DELETE == kind) { // removed bag
                                connector.fireRemoveBagEvent(path);
                            } else if (ENTRY_MODIFY == kind) { // changed bag
                                logger.info("bag entry modified, sending modified node event for bag: " +
                                        path);
                                connector.fireModifiedBagEvent(path);
                            }
                        } else if (ManifestUtil.isManifest(path)) {
                            if (ENTRY_CREATE == kind) {
                                logger.info("new manifest, send new node event for bag: " +
                                        path.getParent());
                                connector.fireNewBagEvent(path.getParent());
                            } else if (ENTRY_DELETE == kind) {
                                logger.info("manifest gone, send remove node event for bag: " +
                                        path.getParent());
                                connector.fireRemoveBagEvent(path.getParent());
                            } else if (ENTRY_MODIFY == kind) {
                                logger.info("manifest modified, sending modified node event for bag: " +
                                        path.getParent());
                                connector
                                        .fireModifiedBagEvent(path.getParent());
                            }
                            // final Boolean manifest = true;
                        } else if (ManifestUtil.isTagManifest(path)) {
                            // final Boolean tagManifest = true;
                        } else {
                            logger.warn("Unrecognized event at: " +
                                    path.toAbsolutePath());
                        }
                    }
                    key.reset();
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
                this.bagItDir);
        this.shutdown = true;
    }
}
