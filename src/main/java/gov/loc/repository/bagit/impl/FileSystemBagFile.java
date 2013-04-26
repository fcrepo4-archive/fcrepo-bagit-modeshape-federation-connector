
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.DeclareCloseable;
import gov.loc.repository.bagit.filesystem.FileNode;

import java.io.Closeable;
import java.io.InputStream;

public class FileSystemBagFile implements BagFile, DeclareCloseable {

    private final String filepath;

    private final FileNode fileNode;

    public FileSystemBagFile(final String filepath, final FileNode fileNode) {
        this.filepath = filepath;
        this.fileNode = fileNode;
    }

    public FileNode getFileNode() {
        return this.fileNode;
    }

    @Override
    public InputStream newInputStream() {
        return this.fileNode.newInputStream();
    }

    @Override
    public String getFilepath() {
        return this.filepath;
    }

    @Override
    public boolean exists() {
        return this.fileNode.exists();
    }

    @Override
    public long getSize() {
        return this.fileNode.getSize();
    }

    @Override
    public Closeable declareCloseable() {
        return this.fileNode.getFileSystem();
    }
}
