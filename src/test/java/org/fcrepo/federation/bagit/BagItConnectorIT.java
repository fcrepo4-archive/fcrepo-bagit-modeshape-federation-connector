
package org.fcrepo.federation.bagit;

import static org.fcrepo.jaxb.responses.access.ObjectProfile.ObjectStates.A;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.fcrepo.FedoraObject;
import org.fcrepo.jaxb.responses.access.ObjectProfile;
import org.fcrepo.services.PathService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modeshape.jcr.JcrSession;
import org.modeshape.jcr.api.Session;
import org.modeshape.jcr.api.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-test/master.xml")
public class BagItConnectorIT {
	private static Logger logger = LoggerFactory.getLogger(BagItConnectorIT.class);

    @Inject
    Repository repo;

    @Test
	public void tryProgrammaticAccess() throws RepositoryException {
		Session session = (Session)repo.login();
		Node node = session.getNode("/objects/BagItFed1");
		logger.info("Got node at " + node.getPath());
		PropertyIterator properties = node.getProperties("bagit:*");
		assertTrue(properties.hasNext());
		// Bag-Count: 1 of 1
		Property property = node.getProperty("bagit:Bag.Count");
		assertNotNull(property);
		assertEquals("1 of 1", property.getString());
		NodeIterator nodes = node.getNodes();
		assertTrue("/objects/testDS had no child nodes!", nodes.hasNext());
		Node child = nodes.nextNode();
		nodes = child.getNodes();
		assertEquals("jcr:content", nodes.nextNode().getName());
		FedoraObject obj = new FedoraObject(session, PathService.getObjectJcrNodePath("BagItFed1"));
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
		JcrSession session = (JcrSession)repo.login();

		// create a random bag and move it into the federated directory
		File baseDir = new File( "./target/test-classes" );
        File srcDir = new File( baseDir, "tmp-objects" );
		File dstDir = new File( baseDir, "test-objects" );
		long fileSize = 1024L;
        makeRandomBags( srcDir, 1, 1, fileSize );
		File srcBag = new File( srcDir, "randomBag0" );
		File dstBag = new File( dstDir, "randomBag0" );
		srcBag.renameTo( dstBag );

		// check that the bag shows up in the federation
		Node node = session.getNode("/objects/randomBag0");
		logger.info("Got node at " + node.getPath());
		PropertyIterator properties = node.getProperties();
		assertTrue(properties.hasNext());
		Property property = node.getProperty("bagit:Bag.Size");
		assertNotNull(property);
		NodeIterator nodes = node.getNodes();
		assertTrue("/objects/randomBag0 had no child nodes!", nodes.hasNext());
		Node child = nodes.nextNode();
		nodes = child.getNodes();
		assertEquals("jcr:content", nodes.nextNode().getName());
		FedoraObject obj = new FedoraObject(session, PathService.getObjectJcrNodePath("BagItFed1"));
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
	static void makeRandomBags( File baseDir, int bagCount,
		int fileCount, long fileSize ) throws IOException
	{
		BagFactory factory = new BagFactory();
		DefaultCompleter completer = new DefaultCompleter(factory);
		FileSystemWriter writer = new FileSystemWriter(factory);
		for ( int i = 0; i < bagCount; i++ )
		{
			logger.debug("Creating random bag: " + i);
			File bagDir = new File( baseDir, "randomBag" + i);
			File dataDir = new File( bagDir, "data" );
			if ( !dataDir.exists() )
			{
				dataDir.mkdirs();
			}
			for ( int j = 0; j < fileCount; j++ )
			{
				File dataFile = new File( dataDir, "randomFile" + j );
				BufferedWriter buf = new BufferedWriter( new FileWriter(dataFile) );
				for ( long k = 0L; k < fileSize; k++ )
				{
					buf.write( String.valueOf((int)(Math.random() * 10)) );
				}
				buf.close();
			}
			PreBag pre = factory.createPreBag( bagDir );
			Bag bag = pre.makeBagInPlace( BagFactory.LATEST, true, completer );
			bag.write( writer, bagDir );
		}
	}
}
