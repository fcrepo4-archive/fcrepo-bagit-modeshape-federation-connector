
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.BagFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileBagFile implements BagFile {

    private final File file;

    private final String filepath;

    public FileBagFile(final String name, final File file) {
        this.filepath = name;
        this.file = file;

    }

    @Override
    public InputStream newInputStream() {

        try {
            return new BufferedInputStream(new FileInputStream(this.file));
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getFilepath() {
        return this.filepath;
    }

    @Override
    public boolean exists() {
        if (this.file != null && this.file.exists()) {
            return true;
        }
        return false;
    }

    @Override
    public long getSize() {
        if (this.exists()) {
            return this.file.length();
        }
        return 0L;
    }

    public File getFile() {
        return file;
    }
}
