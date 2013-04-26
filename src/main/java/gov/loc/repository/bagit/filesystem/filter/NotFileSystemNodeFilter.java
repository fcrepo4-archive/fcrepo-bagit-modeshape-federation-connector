
package gov.loc.repository.bagit.filesystem.filter;

import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.FileSystemNodeFilter;

public class NotFileSystemNodeFilter implements FileSystemNodeFilter {

    private final FileSystemNodeFilter filter;

    public NotFileSystemNodeFilter(final FileSystemNodeFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean accept(final FileSystemNode fileSystemNode) {
        return !filter.accept(fileSystemNode);
    }

}
