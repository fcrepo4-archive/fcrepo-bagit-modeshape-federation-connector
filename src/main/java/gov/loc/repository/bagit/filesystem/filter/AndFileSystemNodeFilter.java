
package gov.loc.repository.bagit.filesystem.filter;

import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.FileSystemNodeFilter;

public class AndFileSystemNodeFilter implements FileSystemNodeFilter {

    private final FileSystemNodeFilter[] filters;

    public AndFileSystemNodeFilter(final FileSystemNodeFilter... filters) {
        this.filters = filters;
    }

    @Override
    public boolean accept(final FileSystemNode fileSystemNode) {
        for (final FileSystemNodeFilter filter : this.filters) {
            if (!filter.accept(fileSystemNode)) {
                return false;
            }
        }
        return true;
    }

}
