
package gov.loc.repository.bagit.filesystem.impl;

import gov.loc.repository.bagit.filesystem.FileNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

public class ZipFileNode extends AbstractZipNode implements FileNode {

    private final ZipArchiveEntry entry;

    protected ZipFileNode(final ZipArchiveEntry entry, final String filepath,
            final ZipFileSystem fileSystem) {
        super(filepath, fileSystem);
        this.entry = entry;
    }

    public ZipArchiveEntry getEntry() {
        return this.entry;
    }

    @Override
    public long getSize() {
        return this.entry.getSize();
    }

    @Override
    public InputStream newInputStream() {
        if (this.entry == null) {
            throw new RuntimeException("Does not exist");
        }

        try {
            return this.fileSystem.getZipfile().getInputStream(this.entry);
        } catch (final ZipException e) {
            throw new RuntimeException(e);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists() {
        return this.entry != null;
    }

}
