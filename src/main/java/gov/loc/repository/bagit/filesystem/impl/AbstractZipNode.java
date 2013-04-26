
package gov.loc.repository.bagit.filesystem.impl;

import gov.loc.repository.bagit.filesystem.FileSystem;
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.utilities.FilenameHelper;

public class AbstractZipNode implements FileSystemNode {

    protected String filepath;

    protected String name = null;

    protected ZipFileSystem fileSystem;

    protected AbstractZipNode(final String filepath,
            final ZipFileSystem fileSystem) {
        this.filepath = filepath;
        //Root
        if (!filepath.equals("")) {
            this.name = FilenameHelper.getName(filepath);
        }
        this.fileSystem = fileSystem;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFilepath() {
        return this.filepath;
    }

    @Override
    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractZipNode)) {
            return false;
        }
        final AbstractZipNode that = (AbstractZipNode) obj;
        return this.filepath.equals(that.getFilepath());

    }

    @Override
    public int hashCode() {
        return 23 + this.filepath.hashCode();
    }

    @Override
    public boolean isSymlink() {
        return false;
    }

}
