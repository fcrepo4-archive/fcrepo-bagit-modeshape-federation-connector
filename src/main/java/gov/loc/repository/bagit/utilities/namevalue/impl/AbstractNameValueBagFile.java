
package gov.loc.repository.bagit.utilities.namevalue.impl;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;
import gov.loc.repository.bagit.utilities.namevalue.NameValueReader;
import gov.loc.repository.bagit.utilities.namevalue.NameValueReader.NameValue;
import gov.loc.repository.bagit.utilities.namevalue.NameValueWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;

import org.apache.commons.io.IOUtils;

public abstract class AbstractNameValueBagFile extends
        LinkedHashMap<String, String> implements BagFile {

    private static final long serialVersionUID = 1L;

    String filepath;

    BagFile sourceBagFile = null;

    String originalFixity = null;

    String encoding;

    public AbstractNameValueBagFile(final String filepath,
            final BagFile bagFile, final String encoding) {
        this.filepath = filepath;
        this.sourceBagFile = bagFile;
        this.encoding = encoding;
        final NameValueReader reader =
                new NameValueReaderImpl(encoding, sourceBagFile
                        .newInputStream(), this.getType());
        while (reader.hasNext()) {
            final NameValue nameValue = reader.next();
            this.put(nameValue.getName(), nameValue.getValue());
        }
        //Generate original fixity
        this.originalFixity =
                MessageDigestHelper.generateFixity(this.generatedInputStream(),
                        Algorithm.MD5);
    }

    public AbstractNameValueBagFile(final String filepath, final String encoding) {
        this.filepath = filepath;
        this.encoding = encoding;
    }

    @Override
    public String getFilepath() {
        return this.filepath;
    }

    @Override
    public InputStream newInputStream() {
        //If this hasn't changed, then return sourceBagFile's inputstream
        //Otherwise, generate a new inputstream
        //This is to account for junk in the file, e.g., LF/CRs that might effect the fixity of this manifest
        if (MessageDigestHelper.fixityMatches(this.generatedInputStream(),
                Algorithm.MD5, this.originalFixity)) {
            return this.sourceBagFile.newInputStream();
        }
        return this.generatedInputStream();
    }

    InputStream generatedInputStream() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final NameValueWriter writer =
                new NameValueWriterImpl(out, this.encoding, this.getType());
        try {
            for (final String name : this.keySet()) {
                writer.write(name, this.get(name));
            }
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public boolean exists() {
        return true;
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

    public abstract String getType();

}
