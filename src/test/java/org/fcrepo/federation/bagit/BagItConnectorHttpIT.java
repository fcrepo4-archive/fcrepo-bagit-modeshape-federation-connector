
package org.fcrepo.federation.bagit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.File;

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
	public void tryFilesystemUpdates() throws Exception {
        // create a random bag and move it into the federated directory
        File baseDir = new File( "./target/test-classes" );
        File srcDir = new File( baseDir, "tmp-objects" );
        File dstDir = new File( baseDir, "test-objects" );
        long fileSize = 1024L;
        BagItConnectorIT.makeRandomBags( srcDir, 1, 1, fileSize );
        File srcBag = new File( srcDir, "randomBag0" );
        File dstBag = new File( dstDir, "randomBagRest" );
        srcBag.renameTo( dstBag );

        // check object profile
        final HttpGet objProfileGet = new HttpGet(
            serverAddress + "objects/randomBagRest");
        String objProfile = EntityUtils.toString(
            client.execute(objProfileGet).getEntity());
		logger.debug("objProfile: " + objProfile);
        assertNotNull( "randomBagRest not found", objProfile );

        // check datastream list
        final HttpGet dsListGet = new HttpGet(
            serverAddress + "objects/randomBagRest/datastreams/");
        String dsList = EntityUtils.toString(
            client.execute(dsListGet).getEntity());
		logger.debug("dsList: " + dsList);
        assertTrue( "randomFile0 not in datastream list",
			dsList.indexOf("randomFile0") != -1 );

        // check datastream profile
        final HttpGet dsProfileGet = new HttpGet( serverAddress
            + "objects/randomBagRest/datastreams/randomFile0");
        String dsProfile = EntityUtils.toString(
            client.execute(dsProfileGet).getEntity());
        logger.debug("dsProfile: " + dsProfile);
        assertTrue("Datastream empty", dsProfile != null );

        // check datastream content
        final HttpGet dsContentGet = new HttpGet( serverAddress
            + "objects/randomBagRest/datastreams/randomFile0/content");
        String dsContent = EntityUtils.toString(
            client.execute(dsContentGet).getEntity());
        logger.debug("dsContent: '" + dsContent + "'");
        assertTrue("Datastream empty", dsContent != null && dsContent.length() == fileSize);
	}
}
