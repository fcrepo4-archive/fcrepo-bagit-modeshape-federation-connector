
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.Bag.BagPartFactory;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagInfoTxtReader;
import gov.loc.repository.bagit.BagInfoTxtWriter;
import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.BagItTxtReader;
import gov.loc.repository.bagit.BagItTxtWriter;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxtReader;
import gov.loc.repository.bagit.FetchTxtWriter;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.ManifestWriter;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractBagPartFactory implements BagPartFactory {

    protected BagConstants bagConstants;

    protected BagFactory bagFactory;

    public AbstractBagPartFactory(final BagFactory bagFactory,
            final BagConstants bagConstants) {
        this.bagConstants = bagConstants;
        this.bagFactory = bagFactory;

    }

    @Override
    public BagItTxt createBagItTxt(final BagFile bagFile) {
        return new BagItTxtImpl(bagFile, this.bagConstants);
    }

    @Override
    public BagItTxt createBagItTxt() {
        return new BagItTxtImpl(this.bagConstants);
    }

    @Override
    public BagItTxtReader createBagItTxtReader(final String encoding,
            final InputStream in) {
        return new BagItTxtReaderImpl(encoding, in);
    }

    @Override
    public BagItTxtWriter
            createBagItTxtWriter(final OutputStream out, final String encoding,
                    final int lineLength, final int indentSpaces) {
        return new BagItTxtWriterImpl(out, encoding, lineLength, indentSpaces);
    }

    @Override
    public BagItTxtWriter createBagItTxtWriter(final OutputStream out,
            final String encoding) {
        return new BagItTxtWriterImpl(out, encoding);
    }

    @Override
    public Manifest createManifest(final String name) {
        return new ManifestImpl(name, this.bagConstants, this);
    }

    @Override
    public Manifest createManifest(final String name,
            final BagFile sourceBagFile) {
        return new ManifestImpl(name, this.bagConstants, this, sourceBagFile);
    }

    @Override
    public ManifestWriter createManifestWriter(final OutputStream out,
            final String manifestSeparator) {
        if (manifestSeparator != null) {
            return new ManifestWriterImpl(out, manifestSeparator);
        }
        return this.createManifestWriter(out);
    }

    @Override
    public BagInfoTxt createBagInfoTxt() {
        return new BagInfoTxtImpl(this.bagConstants);
    }

    @Override
    public BagInfoTxt createBagInfoTxt(final BagFile bagFile) {
        return new BagInfoTxtImpl(bagFile, this.bagConstants);
    }

    @Override
    public BagInfoTxtReader createBagInfoTxtReader(final String encoding,
            final InputStream in) {
        return new BagInfoTxtReaderImpl(encoding, in);
    }

    @Override
    public BagInfoTxtWriter createBagInfoTxtWriter(final OutputStream out,
            final String encoding) {
        return new BagInfoTxtWriterImpl(out, encoding);
    }

    @Override
    public BagInfoTxtWriter
            createBagInfoTxtWriter(final OutputStream out,
                    final String encoding, final int lineLength,
                    final int indentSpaces) {
        return new BagInfoTxtWriterImpl(out, encoding, lineLength, indentSpaces);
    }

    @Override
    public FetchTxt createFetchTxt() {
        return new FetchTxtImpl(this.bagConstants, this);
    }

    @Override
    public FetchTxt createFetchTxt(final BagFile sourceBagFile) {
        return new FetchTxtImpl(this.bagConstants, this, sourceBagFile);
    }

    @Override
    public FetchTxtReader createFetchTxtReader(final InputStream in,
            final String encoding) {
        return new FetchTxtReaderImpl(in, encoding);
    }

    @Override
    public FetchTxtWriter createFetchTxtWriter(final OutputStream out) {
        return new FetchTxtWriterImpl(out);
    }

}
