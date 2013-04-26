
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagInfoTxtReader;
import gov.loc.repository.bagit.utilities.namevalue.impl.NameValueReaderImpl;

import java.io.InputStream;

public class BagInfoTxtReaderImpl extends NameValueReaderImpl implements
        BagInfoTxtReader {

    public BagInfoTxtReaderImpl(final String encoding, final InputStream in) {
        super(encoding, in, BagInfoTxt.TYPE);
    }

}
