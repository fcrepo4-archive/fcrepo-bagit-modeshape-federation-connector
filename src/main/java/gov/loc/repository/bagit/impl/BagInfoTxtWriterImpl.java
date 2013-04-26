
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagInfoTxtWriter;
import gov.loc.repository.bagit.utilities.namevalue.impl.NameValueWriterImpl;

import java.io.OutputStream;

public class BagInfoTxtWriterImpl extends NameValueWriterImpl implements
        BagInfoTxtWriter {

    public BagInfoTxtWriterImpl(final OutputStream out, final String encoding) {
        super(out, encoding, BagInfoTxt.TYPE);
    }

    public BagInfoTxtWriterImpl(final OutputStream out, final String encoding,
            final int lineLength, final int indentSpaces) {
        super(out, encoding, lineLength, indentSpaces, BagInfoTxt.TYPE);
    }
}
