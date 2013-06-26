
package org.fcrepo.federation.bagit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.common.logging.Logger;
import org.modeshape.jcr.ExecutionContext;
import org.modeshape.jcr.api.nodetype.NodeTypeManager;
import org.modeshape.jcr.federation.spi.Connector;
import org.modeshape.jcr.federation.spi.DocumentWriter;

public class BagItConnectorTest {

    BagItConnector testObj;

    DocumentWriterFactory mockFactory;

    DocumentWriter mockWriter;

    Logger mockLogger;

    File tempDir;

    @Before
    public void setUp() throws NoSuchFieldException, SecurityException,
        IllegalArgumentException, IllegalAccessException, RepositoryException,
        IOException {
        testObj = new BagItConnector();
        mockFactory = mock(DocumentWriterFactory.class);
        mockWriter = mock(DocumentWriter.class);
        mockLogger = mock(Logger.class);

        final Field logger = Connector.class.getDeclaredField("logger");
        logger.setAccessible(true);
        logger.set(testObj, mockLogger);
        tempDir =
                File.createTempFile("bagit", Long.toString(System.nanoTime()));
        tempDir.delete();
        tempDir.mkdirs();
        final Field dirPath =
                BagItConnector.class.getDeclaredField("directoryPath");
        dirPath.setAccessible(true);
        dirPath.set(testObj, tempDir.getAbsolutePath());
        final NamespaceRegistry mockReg = mock(NamespaceRegistry.class);
        final NodeTypeManager mockNodeTypes = mock(NodeTypeManager.class);
        testObj.initialize(mockReg, mockNodeTypes);
        testObj.m_writerFactory = mockFactory;
        final Field context = Connector.class.getDeclaredField("context");
        context.setAccessible(true);
        context.set(testObj, ExecutionContext.DEFAULT_CONTEXT);
    }

    @After
    public void tearDown() {
        if (testObj != null) {
            testObj.shutdown();
        }
        testObj = null;
        tempDir.delete();
    }

    @Test
    public void testGetBagInfo() throws IOException {
        final File foo = new File(tempDir, "foo");
        foo.mkdirs();
        BagInfo bi = testObj.getBagInfo("/foo");
        assertTrue(bi == null);
        touch(new File(foo, "bag-info.txt"));
        bi = testObj.getBagInfo("/foo");
        assertNotNull(bi);
    }

    @Test
    public void getDocumentById() throws IOException {
        final File data = new File(new File(tempDir, "foo"), "data");
        data.mkdirs();
        touch(new File(data, "bar"));
        when(mockFactory.getDocumentWriter(any(String.class))).thenReturn(
                mockWriter);
        testObj.getDocumentById("/foo");
        verify(mockFactory).getDocumentWriter("/foo");
        verify(mockWriter).setParent(eq("/"));
        testObj.getDocumentById("/foo/bar");
        verify(mockFactory).getDocumentWriter("/foo/bar");
        verify(mockWriter).setParent(eq("/foo"));
    }

    @Test
    public void testFileFor() throws IOException {
        final File data = new File(new File(tempDir, "foo"), "data");
        data.mkdirs();
        touch(new File(data, "bar"));
        final File result = testObj.fileFor("/foo/bar");
        assertTrue(result.exists());
        assertEquals(result.getParent(), data.getAbsolutePath());
    }

    @Test
    public void testIdFor() throws IOException {
        new File(tempDir, "foo/data").mkdirs();
        final File input = (new File(tempDir, "foo/data/bar"));
        final String result = testObj.idFor(input);
        assertEquals(result, "/foo/bar");
    }

    static void touch(final File file) throws IOException {
        final FileOutputStream out = new FileOutputStream(file);
        out.write(new byte[0]);
        out.flush();
        out.close();
    }
}
