
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.BagItTxtReader;
import gov.loc.repository.bagit.utilities.namevalue.impl.NameValueReaderImpl;

import java.io.InputStream;

public class BagItTxtReaderImpl extends NameValueReaderImpl implements
        BagItTxtReader {

    public BagItTxtReaderImpl(final String encoding, final InputStream in) {
        super(encoding, in, BagItTxt.TYPE);
    }

}
