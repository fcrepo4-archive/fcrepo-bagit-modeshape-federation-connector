
package org.fcrepo.federation.bagit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;
import org.fcrepo.FedoraObject;
import org.fcrepo.services.PathService;
import org.junit.Test;
import org.modeshape.jcr.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BagItConnectorHttpIT extends AbstractResourceIT {
	private static Logger logger = LoggerFactory.getLogger(BagItConnectorHttpIT.class);
	
    @Test
	public void tryOneObject() throws ClientProtocolException, IOException {
        logger.debug("Found objects: " +
                EntityUtils.toString(client.execute(
                        new HttpGet(serverAddress + "objects/")).getEntity()));
        final String objName = "BagItFed1";
        final HttpResponse response =
                client.execute(new HttpGet(serverAddress + "objects/" + objName));
        String msg = EntityUtils.toString(response.getEntity());
        System.out.println(msg);
        assertEquals(response.getStatusLine().getReasonPhrase(), 200, response
                .getStatusLine().getStatusCode());
    }

	@Test
	public void testExistingBag() throws Exception {
		testObject( "BagItFed1", "testDS", 18L );
	}

	@Test
	public void testMovedBag() throws Exception {
        // create a random bag and move it into the federated directory
        File baseDir = new File( "./target/test-classes" );
        File srcDir = new File( baseDir, "tmp-objects" );
        File dstDir = new File( baseDir, "test-objects" );
        long fileSize = 1024L;
        BagItConnectorIT.makeRandomBags( srcDir, 1, 1, fileSize );
        File srcBag = new File( srcDir, "randomBag0" );
        File dstBag = new File( dstDir, "randomBagRest" );
        srcBag.renameTo( dstBag );

		// test the moved bag
		testObject( "randomBagRest", "randomFile0", fileSize );
	}

	private void testObject( String pid, String existingDS,
		long existingDSSize ) throws Exception
	{
        // check object profile
        final HttpGet objProfileGet = new HttpGet(
            serverAddress + "objects/" + pid);
		final HttpResponse objProfileResp = client.execute(objProfileGet);
        String objProfile = EntityUtils.toString(objProfileResp.getEntity());
		logger.debug("objProfile: " + objProfile);
		assertEquals("Object not found: " + pid,
			200, objProfileResp.getStatusLine().getStatusCode());
        assertNotNull( "Blank profile: " + pid, objProfile );

		// check existing datastream exists
		if ( existingDS != null )
		{
        	// check datastream list
        	final HttpGet dsListGet = new HttpGet(
            	serverAddress + "objects/" + pid + "/datastreams/");
        	String dsList = EntityUtils.toString(
            	client.execute(dsListGet).getEntity());
			logger.debug("dsList: " + dsList);
        	assertTrue( "Existing datastream not in datastream list",
				dsList.indexOf(existingDS) != -1 );

        	// check datastream profile
        	final HttpGet dsProfileGet = new HttpGet( serverAddress
            	+ "objects/" + pid + "/datastreams/" + existingDS);
        	String dsProfile = EntityUtils.toString(
            	client.execute(dsProfileGet).getEntity());
        	logger.debug("dsProfile: " + dsProfile);
        	assertTrue("Existing datastream profile empty", dsProfile != null );
	
        	// check datastream content
        	final HttpGet dsContentGet = new HttpGet( serverAddress
            	+ "objects/" + pid + "/datastreams/" + existingDS + "/content");
        	String dsContent = EntityUtils.toString(
            	client.execute(dsContentGet).getEntity());
        	logger.debug("dsContent: '" + dsContent + "'");
        	assertTrue("Existing datastream content empty",
				dsContent != null && dsContent.length() == existingDSSize);

			/* TODO: implement write functionality in BagItConnector

			// update datastream content
			String updatedContent = "This is some updated content";
			final HttpPut dsContentPut = putDSMethod(
				pid,existingDS,updatedContent);
			HttpResponse updateResponse = client.execute(dsContentPut);
			assertEquals("Existing datastream not updated",
				201, updateResponse.getStatusLine().getStatusCode());

        	// check datastream content
        	final HttpGet updatedContentGet = new HttpGet( serverAddress
            	+ "objects/" + pid + "/datastreams/" + existingDS + "/content");
        	String actualContent = EntityUtils.toString(
            	client.execute(updatedContentGet).getEntity());
        	logger.debug("actual content: '" + actualContent + "'");
        	assertTrue("Existing datastream update didn't work",
				actualContent != null && actualContent.equals(updatedContent) );
			*/
		}

		/* TODO: implement write functionality in BagItConnector

		// add a datastream
		String newDSContent = "This is a new datastream";
		final HttpPost dsContentPost = postDSMethod(
			pid, "newDS", newDSContent );
		final HttpResponse newDSResponse = client.execute(dsContentPost);
		logger.debug("newDSLocation: "
			+ newDSResponse.getFirstHeader("Location").getValue());
		assertEquals("New datastream not created",
			201, newDSResponse.getStatusLine().getStatusCode());

        // check new datastream profile
        final HttpGet newDSProfileGet = new HttpGet( serverAddress
            + "objects/" + pid + "/datastreams/newDS");
        String newDSProfile = EntityUtils.toString(
            client.execute(newDSProfileGet).getEntity());
        logger.debug("newDSProfile: " + newDSProfile);
        assertTrue("New Datastream empty", newDSProfile != null );

        // check new datastream content
        final HttpGet restDSContentGet = new HttpGet( serverAddress
            + "objects/" + pid + "/datastreams/newDS/content");
        String restDSContent = EntityUtils.toString(
            client.execute(restDSContentGet).getEntity());
        logger.debug("restDSContent: '" + restDSContent + "'");
		assertEquals("New datastream REST content mismatch",
			restDSContent, restDSContent );

		// check datastream is written to bag on disk
        File newDSFile = new File("target/test-classes/" + pid + "/data/newDS");
		logger.debug("newDSFile: " + newDSFile.getAbsolutePath() );
		assertTrue("New datastream not written to bag", newDSFile.exists() );

		// check updated content can be retrieved
		String actualContent = null;
		BufferedReader buf = new BufferedReader( new FileReader(newDSFile) );
		actualContent = buf.readLine();
		buf.close();
		logger.debug("actualContent: '" + actualContent + "'");
		assertEquals("Datastream content on disk doesn't match",
			actualContent, newDSContent);

		*/
	}
}
