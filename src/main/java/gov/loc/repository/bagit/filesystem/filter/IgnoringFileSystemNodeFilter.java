
package gov.loc.repository.bagit.filesystem.filter;

import gov.loc.repository.bagit.filesystem.DirNode;
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.FileSystemNodeFilter;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.util.List;

public class IgnoringFileSystemNodeFilter implements FileSystemNodeFilter {

    private final List<String> ignoreAdditionalDirectories;

    private final boolean ignoreSymlinks;

    private String relativeFilepath = null;

    public IgnoringFileSystemNodeFilter(
            final List<String> ignoreAdditionalDirectories,
            final boolean ignoreSymlinks) {
        assert ignoreAdditionalDirectories != null;
        this.ignoreAdditionalDirectories = ignoreAdditionalDirectories;
        this.ignoreSymlinks = ignoreSymlinks;
    }

    public void setRelativeFilepath(final String relativeFilepath) {
        this.relativeFilepath = relativeFilepath;
    }

    @Override
    public boolean accept(final FileSystemNode fileSystemNode) {
        String filepath = fileSystemNode.getFilepath();
        if (relativeFilepath != null) {
            filepath =
                    FilenameHelper.removeBasePath(relativeFilepath, filepath);
        }
        if (this.ignoreSymlinks && fileSystemNode.isSymlink()) {
            return false;
        }
        if ((fileSystemNode instanceof DirNode) &&
                this.ignoreAdditionalDirectories.contains(filepath)) {
            return false;
        }
        return true;
    }

}
