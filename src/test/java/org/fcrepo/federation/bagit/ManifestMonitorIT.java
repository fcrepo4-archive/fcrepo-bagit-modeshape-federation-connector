
package org.fcrepo.federation.bagit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.slf4j.LoggerFactory.getLogger;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.ObservationManager;

import org.apache.commons.io.FileUtils;
import org.fcrepo.federation.bagit.LoggingEventListener.EventLogger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.modeshape.jcr.JcrRepositoryFactory;
import org.slf4j.Logger;

public class ManifestMonitorIT {

    private static Logger logger = getLogger(ManifestMonitorIT.class);

    Repository repo;

    @Before
    public void setUp() throws RepositoryException {
        repo = new JcrRepositoryFactory().getRepository("file:/src/test/resources/test_repository.json", "repo");
    }


    @Test
    public void testEventsReceived() throws Exception {
    	// event listener settings and session
        Session listenerSession = repo.login("default");
        ObservationManager observationManager = listenerSession.getWorkspace().getObservationManager();
        boolean isDeep = true; // if outputPath is ancestor of the sequencer output, false if identical
        String[] uuids = null; // Don't care about UUIDs of nodes for sequencing events
        String[] nodeTypes = null; // Don't care about node types of output nodes for sequencing events
        boolean noLocal = false; // We do want events for sequencing happen locally (as well as remotely)

        // Now create a listener implementation that will be called for our bag created event..
        CountDownLatch addlatch = new CountDownLatch(1);
        EventLogger addlogger = Mockito.mock(LoggingEventListener.EventLogger.class);
        LoggingEventListener addlistener = new LoggingEventListener(addlatch, addlogger);
        String outputPath = "/objects";
        observationManager.addEventListener(addlistener,Event.NODE_ADDED,outputPath,isDeep,
                                            uuids, nodeTypes, noLocal);
        // create a random bag and move it into the federated directory
        final File baseDir = new File("./target/test-classes");
        final File srcDir = new File(baseDir, "tmp-objects");
        final File dstDir = new File(baseDir, "test-objects");
        final long fileSize = 1024L;
        makeRandomBags(srcDir, 1, 1, fileSize);
        final File srcBag = new File(srcDir, "randomBag0");
        final File dstBag = new File(dstDir, "randomBag0");
        srcBag.renameTo(dstBag);
        addlatch.await(15, TimeUnit.SECONDS);
        verify(addlogger, times(1)).log(Mockito.eq(Event.NODE_ADDED), Mockito.eq("/objects/randomBag0"));

        // FIXME test bag update event
        //logger.debug("Now try tinkering with a manifest");
        //final File bagInfo = new File(dstBag, "bag-info.txt");

        // Test bag remove event
        CountDownLatch dellatch = new CountDownLatch(1);
        EventLogger dellogger = Mockito.mock(LoggingEventListener.EventLogger.class);
        LoggingEventListener dellistener = new LoggingEventListener(dellatch, dellogger);
        observationManager.addEventListener(dellistener,Event.NODE_REMOVED,outputPath,isDeep,
                                            uuids, nodeTypes, noLocal);
        FileUtils.deleteDirectory(dstBag);
        dellatch.await(30, TimeUnit.SECONDS);
        verify(dellogger, times(1)).log(Mockito.eq(Event.NODE_REMOVED), Mockito.eq("/objects/randomBag0"));

        listenerSession.logout();
    }

    static void makeRandomBags(final File baseDir, final int bagCount,
            final int fileCount, final long fileSize) throws IOException {
        final BagFactory factory = new BagFactory();
        final DefaultCompleter completer = new DefaultCompleter(factory);
        final FileSystemWriter writer = new FileSystemWriter(factory);
        for (int i = 0; i < bagCount; i++) {
            logger.debug("Creating random bag: " + i);
            final File bagDir = new File(baseDir, "randomBag" + i);
            final File dataDir = new File(bagDir, "data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            for (int j = 0; j < fileCount; j++) {
                final File dataFile = new File(dataDir, "randomFile" + j);
                final BufferedWriter buf =
                        new BufferedWriter(new FileWriter(dataFile));
                for (long k = 0L; k < fileSize; k++) {
                    buf.write(String.valueOf((int) (Math.random() * 10)));
                }
                buf.close();
            }
            final PreBag pre = factory.createPreBag(bagDir);
            final Bag bag =
                    pre.makeBagInPlace(BagFactory.LATEST, true, completer);
            bag.write(writer, bagDir);
        }
    }
}
