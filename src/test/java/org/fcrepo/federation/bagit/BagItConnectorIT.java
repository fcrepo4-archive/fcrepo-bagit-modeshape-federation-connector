
package org.fcrepo.federation.bagit;

import static org.fcrepo.jaxb.responses.access.ObjectProfile.ObjectStates.A;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.fcrepo.FedoraObject;
import org.fcrepo.jaxb.responses.access.ObjectProfile;
import org.fcrepo.services.PathService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.jcr.JcrSession;
import org.modeshape.jcr.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-test/master.xml")
public class BagItConnectorIT {

    private static Logger logger = LoggerFactory
            .getLogger(BagItConnectorIT.class);

    @Inject
    Repository repo;

    @Test
    public void tryProgrammaticAccess() throws RepositoryException {
        final Session session = (Session) repo.login();
        final Node node = session.getNode("/objects/BagItFed1");
        logger.info("Got node at " + node.getPath());
        final PropertyIterator properties = node.getProperties("bagit:*");
        assertTrue(properties.hasNext());
        // Bag-Count: 1 of 1
        final Property property = node.getProperty("bagit:Bag.Count");
        assertNotNull(property);
        assertEquals("1 of 1", property.getString());
        NodeIterator nodes = node.getNodes();
        assertTrue("/objects/testDS had no child nodes!", nodes.hasNext());
        final Node child = nodes.nextNode();
        nodes = child.getNodes();
        assertEquals("jcr:content", nodes.nextNode().getName());
        final FedoraObject obj =
                new FedoraObject(session, PathService
                        .getObjectJcrNodePath("BagItFed1"));
        final ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.pid = obj.getName();
        objectProfile.objLabel = obj.getLabel();
        objectProfile.objOwnerId = obj.getOwnerId();
        objectProfile.objCreateDate = obj.getCreated();
        objectProfile.objLastModDate = obj.getLastModified();
        objectProfile.objSize = obj.getSize();
        //        objectProfile.objItemIndexViewURL =
        //                uriInfo.getAbsolutePathBuilder().path("datastreams").build();
        objectProfile.objState = A;
        objectProfile.objModels = obj.getModels();
    }

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
        final FedoraObject obj =
                new FedoraObject(session, PathService
                        .getObjectJcrNodePath("BagItFed1"));
        final ObjectProfile objectProfile = new ObjectProfile();
        objectProfile.pid = obj.getName();
        objectProfile.objLabel = obj.getLabel();
        objectProfile.objOwnerId = obj.getOwnerId();
        objectProfile.objCreateDate = obj.getCreated();
        objectProfile.objLastModDate = obj.getLastModified();
        objectProfile.objSize = obj.getSize();
        objectProfile.objState = A;
        objectProfile.objModels = obj.getModels();
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
