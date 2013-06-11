
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.modeshape.jcr.JcrRepositoryFactory;
import org.slf4j.Logger;

public class ManifestMonitorIT {

    private static Logger logger = getLogger(ManifestMonitorIT.class);

    Repository repo;

    Session listenerSession;


    final File baseDir = new File("./target/test-classes");
    final File srcDir = new File(baseDir, "tmp-objects");
    final File dstDir = new File(baseDir, "test-objects");
    File dstBag = new File(dstDir, "randomBag0");

    @Before
    public void setUp() throws RepositoryException, IOException {
    	if(dstBag.exists()) FileUtils.deleteDirectory(dstBag);
        repo = new JcrRepositoryFactory().getRepository("file:/src/test/resources/test_repository.json", "repo");
		listenerSession = this.repo.login("default");
    }

    @After
    public void after() throws IOException {
    	if(listenerSession != null) listenerSession.logout();
    	if(dstBag.exists()) FileUtils.deleteDirectory(dstBag);
    }


    @Test
    public void testEventsReceived() throws Exception {
        // Now create a listener implementation that will be called for our bag created event..
        CountDownLatch addlatch = new CountDownLatch(1);
        EventLogger addlogger = getEventLogger(addlatch, Event.NODE_ADDED);
        CountDownLatch updateLatch = new CountDownLatch(1);
        EventLogger updateLogger = getEventLogger(updateLatch, Event.PROPERTY_CHANGED);
        CountDownLatch dellatch = new CountDownLatch(1);
        EventLogger dellogger = getEventLogger(dellatch, Event.NODE_REMOVED);

        // create a random bag and move it into the federated directory
        final long fileSize = 1024L;
        makeRandomBags(srcDir, 1, 1, fileSize);
        final File srcBag = new File(srcDir, "randomBag0");
        srcBag.renameTo(dstBag);
        addlatch.await(15, TimeUnit.SECONDS);
        verify(addlogger, times(1)).log(Mockito.eq(Event.NODE_ADDED), Mockito.eq("/objects/randomBag0"));

        // test bag update event
        final File bagInfo = new File(dstBag, "manifest-md5.txt");
        bagInfo.setLastModified(System.currentTimeMillis());
        updateLatch.await(15, TimeUnit.SECONDS);
        verify(updateLogger, times(1)).log(Mockito.eq(Event.PROPERTY_CHANGED), Mockito.startsWith("/objects/randomBag0"));

        // Test bag remove event
        FileUtils.deleteDirectory(dstBag);
        logger.debug("deleted directory: "+dstBag.getAbsolutePath());
        dellatch.await(15, TimeUnit.SECONDS);
        verify(dellogger, Mockito.atLeastOnce()).log(Mockito.eq(Event.NODE_REMOVED), Mockito.eq("/objects/randomBag0"));
    }

    /**
	 * @param addlatch
	 * @return
	 */
	private EventLogger getEventLogger(CountDownLatch latch, int eventTypes) throws RepositoryException {
        ObservationManager observationManager = this.listenerSession.getWorkspace().getObservationManager();
        boolean isDeep = true; // if outputPath is ancestor of the sequencer output, false if identical
        String[] uuids = null; // Don't care about UUIDs of nodes for sequencing events
        String[] nodeTypes = null; // Don't care about node types of output nodes for sequencing events
        boolean noLocal = false; // We do want events for sequencing happen locally (as well as remotely)
        EventLogger result = Mockito.mock(LoggingEventListener.EventLogger.class);
        LoggingEventListener addlistener = new LoggingEventListener(latch, result);
        String outputPath = "/objects";
        observationManager.addEventListener(addlistener,eventTypes,outputPath,isDeep,
                                            uuids, nodeTypes, noLocal);
		return result;
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
