
package org.fcrepo.federation.bagit;

import gov.loc.repository.bagit.impl.BagInfoTxtWriterImpl;

import java.io.Closeable;
import java.io.OutputStream;

/**
 * Just a proxy to implement Closeable
 * 
 * @author ba2213
 */
public class BagInfoTxtWriter extends BagInfoTxtWriterImpl implements Closeable {

    public BagInfoTxtWriter(final OutputStream out, final String encoding) {
        super(out, encoding);
    }

    public BagInfoTxtWriter(final OutputStream out, final String encoding,
            final int lineLength, final int indent) {
        super(out, encoding, lineLength, indent);
    }

}
