
package gov.loc.repository.bagit.v0_96.impl;

import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.bagit.ManifestWriter;
import gov.loc.repository.bagit.impl.AbstractBagPartFactory;
import gov.loc.repository.bagit.impl.ManifestReaderImpl;
import gov.loc.repository.bagit.impl.ManifestWriterImpl;

import java.io.InputStream;
import java.io.OutputStream;

public class BagPartFactoryImpl extends AbstractBagPartFactory {

    private static final String SPLIT_REGEX = "( \\*)|( \\t)|(\\s+)";

    private static final String SEPARATOR = "  ";

    public BagPartFactoryImpl(final BagFactory bagFactory,
            final BagConstants bagConstants) {
        super(bagFactory, bagConstants);
    }

    @Override
    public ManifestReader createManifestReader(final InputStream in,
            final String encoding) {
        return new ManifestReaderImpl(in, encoding, SPLIT_REGEX, false);
    }

    @Override
    public ManifestReader createManifestReader(final InputStream in,
            final String encoding, final boolean treatBackSlashAsPathSeparator) {
        return new ManifestReaderImpl(in, encoding, SPLIT_REGEX,
                treatBackSlashAsPathSeparator);
    }

    @Override
    public ManifestWriter createManifestWriter(final OutputStream out) {
        return new ManifestWriterImpl(out, SEPARATOR);
    }

    @Override
    public Version getVersion() {
        return Version.V0_96;
    }

}
