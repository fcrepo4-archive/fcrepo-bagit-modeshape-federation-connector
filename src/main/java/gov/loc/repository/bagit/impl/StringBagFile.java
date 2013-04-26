
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.BagFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class StringBagFile implements BagFile {

    private final String filepath;

    private byte[] buf = new byte[0];

    private static final String ENC = "utf-8";

    public StringBagFile(final String name, final byte[] data) {
        this.filepath = name;
        this.buf = data;
    }

    public StringBagFile(final String name, final String str) {
        this.filepath = name;
        if (str != null) {
            try {
                this.buf = str.getBytes(ENC);
            } catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public boolean exists() {
        if (buf.length == 0) {
            return false;
        }
        return true;
    }

    @Override
    public String getFilepath() {
        return this.filepath;
    }

    @Override
    public long getSize() {
        return buf.length;
    }

    @Override
    public InputStream newInputStream() {
        return new ByteArrayInputStream(this.buf);
    }

}
