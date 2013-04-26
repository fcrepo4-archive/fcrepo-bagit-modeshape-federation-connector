
package org.fcrepo.federation.bagit;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.google.common.base.Function;

public class BagItWatchService implements WatchService {

    private static final Logger logger = getLogger(BagItWatchService.class);

    static final Pattern MANIFEST = compile("^manifest-([^\\.]+).txt$");

    static final Pattern TAG_MANIFEST = compile("^tagmanifest-([^\\.]+).txt$");

    static GetFilesFromManifest getFilesFromManifest =
            new GetFilesFromManifest();

    private WatchService delegate;

    private Collection<Path> tagFiles = new ArrayList<Path>();

    Collection<Path> manifests = new ArrayList<Path>();

    BagItWatchService()
            throws IOException {
        delegate = FileSystems.getDefault().newWatchService();
    }

    /**
     * Constructor to facilitate testing
     * @param delegate
     */
    BagItWatchService(final WatchService delegate) {
        this.delegate = delegate;
    }

    public BagItWatchService(final File bagItDir)
            throws IOException {
        this();
        Paths.get(bagItDir.toURI()).register(delegate, ENTRY_CREATE,
                ENTRY_MODIFY, ENTRY_DELETE);
        for (final File file : bagItDir.listFiles()) {
            if (isManifest(file)) {
                monitorManifest(file);
            } else if (isTagManifest(file)) {
                for (final File listedFile : getFilesFromManifest.apply(file)) {
                    monitorTagFile(listedFile);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public WatchKey poll() {
        return delegate.poll();
    }

    @Override
    public WatchKey poll(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return delegate.poll(timeout, unit);
    }

    @Override
    public WatchKey take() throws InterruptedException {
        return delegate.take();
    }

    public void monitorTagFile(final File input) throws IOException {
        final Path path = input.toPath();
        if (!tagFiles.contains(path)) {
            tagFiles.add(path);
            path.register(delegate, ENTRY_MODIFY);
        }
    }

    public void monitorManifest(final File input) throws IOException {
        final Path path = input.toPath();
        if (!manifests.contains(path)) {
            manifests.add(path);
            path.register(delegate, ENTRY_MODIFY);
        }
    }

    boolean isManifest(final String fileName) {
        final Matcher m = MANIFEST.matcher(fileName);
        if (m.find()) {
            final String csa = m.group(1);
            try {
                MessageDigest.getInstance(csa);
                return true;
            } catch (final NoSuchAlgorithmException e) {
                logger.warn(
                        "Ignoring potential manifest file {} because {} is not a supported checksum algorithm.",
                        fileName, csa);
            }
        }
        return false;
    }

    boolean isManifest(final File file) {
        if (file.isFile() && file.canRead() && !file.isHidden()) {
            return (isManifest(file.getName()));
        } else {
            return false;
        }
    }

    boolean isManifest(final Path path) {
        return isManifest(path.toFile());
    }

    boolean isTagManifest(final String fileName) {
        final Matcher m = TAG_MANIFEST.matcher(fileName);
        if (m.find()) {
            final String csa = m.group(1);
            try {
                MessageDigest.getInstance(csa);
                return true;
            } catch (final NoSuchAlgorithmException e) {
                logger.warn(
                        "Ignoring potential tag-manifest file {} because {} is not a supported checksum algorithm.",
                        fileName, csa);
            }
        }
        return false;
    }

    boolean isTagManifest(final File file) {
        if (file.isFile() && file.canRead() && !file.isHidden()) {
            return isTagManifest(file.getName());
        } else {
            return false;
        }
    }

    boolean isTagManifest(final Path path) {
        return isManifest(path.toFile());
    }

    static class GetFilesFromManifest implements
            Function<File, Collection<File>> {

        @Override
        public Collection<File> apply(final File input) {
            try (final LineNumberReader lnr =
                    new LineNumberReader(new FileReader(input))) {
                final ArrayList<File> result = new ArrayList<File>();
                String line;
                while ((line = lnr.readLine()) != null) {
                    final String fileName = line.split(" ")[0];
                    final File file = new File(input.getParentFile(), fileName);
                    result.add(file);
                }
                return result;
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
