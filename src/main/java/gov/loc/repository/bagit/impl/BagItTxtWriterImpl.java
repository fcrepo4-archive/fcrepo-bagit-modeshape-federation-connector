
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.BagItTxtWriter;
import gov.loc.repository.bagit.utilities.namevalue.impl.NameValueWriterImpl;

import java.io.OutputStream;

public class BagItTxtWriterImpl extends NameValueWriterImpl implements
        BagItTxtWriter {

    public BagItTxtWriterImpl(final OutputStream out, final String encoding) {
        super(out, encoding, BagItTxt.TYPE);
    }

    public BagItTxtWriterImpl(final OutputStream out, final String encoding,
            final int lineLength, final int indentSpaces) {
        super(out, encoding, lineLength, indentSpaces, BagItTxt.TYPE);
    }
}
