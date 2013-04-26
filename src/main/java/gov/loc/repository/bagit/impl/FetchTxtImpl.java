
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.Bag.BagPartFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;
import gov.loc.repository.bagit.FetchTxtReader;
import gov.loc.repository.bagit.FetchTxtWriter;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchTxtImpl extends ArrayList<FilenameSizeUrl> implements
        FetchTxt {

    private static final Logger log = LoggerFactory
            .getLogger(FetchTxtImpl.class);

    private static final long serialVersionUID = 1L;

    private String name;

    private BagConstants bagConstants;

    private BagPartFactory bagPartFactory;

    private BagFile sourceBagFile = null;

    private String originalFixity = null;

    public FetchTxtImpl(final BagConstants bagConstants,
            final BagPartFactory bagPartFactory) {
        log.info("Creating new fetch.txt.");
        this.init(bagConstants, bagPartFactory);
    }

    public FetchTxtImpl(final BagConstants bagConstants,
            final BagPartFactory bagPartFactory, final BagFile sourceBagFile) {
        log.info("Creating fetch.txt.");
        this.init(bagConstants, bagPartFactory);
        this.sourceBagFile = sourceBagFile;
        final FetchTxtReader reader =
                bagPartFactory.createFetchTxtReader(sourceBagFile
                        .newInputStream(), this.bagConstants.getBagEncoding());
        try {
            while (reader.hasNext()) {
                this.add(reader.next());
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
        //Generate original fixity
        this.originalFixity =
                MessageDigestHelper.generateFixity(this.generatedInputStream(),
                        Manifest.Algorithm.MD5);
    }

    private void init(final BagConstants bagConstants,
            final BagPartFactory bagPartFactory) {
        this.name = bagConstants.getFetchTxt();
        this.bagConstants = bagConstants;
        this.bagPartFactory = bagPartFactory;
    }

    @Override
    public InputStream newInputStream() {
        //If this hasn't changed, then return sourceBagFile's inputstream
        //Otherwise, generate a new inputstream
        //This is to account for junk in the file, e.g., LF/CRs that might effect the fixity of this manifest
        if (MessageDigestHelper.fixityMatches(this.generatedInputStream(),
                Manifest.Algorithm.MD5, this.originalFixity)) {
            return this.sourceBagFile.newInputStream();
        }
        return this.generatedInputStream();
    }

    private InputStream generatedInputStream() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final FetchTxtWriter writer =
                this.bagPartFactory.createFetchTxtWriter(out);
        try {
            for (final FilenameSizeUrl filenameSizeUrl : this) {
                writer.write(filenameSizeUrl.getFilename(), filenameSizeUrl
                        .getSize(), filenameSizeUrl.getUrl());
            }
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public String getFilepath() {
        return this.name;
    }

    @Override
    public long getSize() {
        final InputStream in = this.newInputStream();
        long size = 0L;
        try {
            while (in.read() != -1) {
                size++;
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return size;
    }

    @Override
    public boolean exists() {
        return true;
    }
}
