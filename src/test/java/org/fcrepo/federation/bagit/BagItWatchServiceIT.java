
package org.fcrepo.federation.bagit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import java.io.Writer;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.jcr.JcrSession;
import org.slf4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-test/master.xml")
public class BagItWatchServiceIT {

    private static Logger logger = getLogger(BagItWatchServiceIT.class);

    @Inject
    Repository repo;

    @Test
    public void tryFilesystemUpdates() throws Exception {
        final JcrSession session = (JcrSession) repo.login();

        // create a random bag and move it into the federated directory
        final File baseDir = new File("./target/test-classes");
        final File srcDir = new File(baseDir, "tmp-objects");
        final File dstDir = new File(baseDir, "test-objects");
        final long fileSize = 1024L;
        makeRandomBags(srcDir, 1, 1, fileSize);
        final File srcBag = new File(srcDir, "randomBag0");
        final File dstBag = new File(dstDir, "randomBag0");
        srcBag.renameTo(dstBag);

        // check that the bag shows up in the federation
        final Node node = session.getNode("/objects/randomBag0");
        logger.info("Got node at " + node.getPath());
        final PropertyIterator properties = node.getProperties();
        assertTrue(properties.hasNext());
        final Property property = node.getProperty("bagit:Bag.Size");
        assertNotNull(property);
        NodeIterator nodes = node.getNodes();
        assertTrue("/objects/randomBag0 had no child nodes!", nodes.hasNext());
        final Node child = nodes.nextNode();
        nodes = child.getNodes();
        assertEquals("jcr:content", nodes.nextNode().getName());

        logger.debug("Now try tinkering with a manifest");
        final File bagInfo = new File(dstBag, "bag-info.txt");
        try (final Writer writer = new FileWriter(bagInfo, true)) {
            writer.write("FAKETAG: FAKETAGVALUE");
        }

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
