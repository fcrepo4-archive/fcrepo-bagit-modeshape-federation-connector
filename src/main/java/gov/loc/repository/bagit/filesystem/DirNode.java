
package gov.loc.repository.bagit.filesystem;

import java.util.Collection;

public interface DirNode extends FileSystemNode {

    Collection<FileSystemNode> listChildren();

    Collection<FileSystemNode> listChildren(final FileSystemNodeFilter filter);

    FileNode childFile(final String name);

    DirNode childDir(final String name);

    Collection<FileSystemNode> listDescendants();

    Collection<FileSystemNode> listDescendants(
            final FileSystemNodeFilter filter,
            final FileSystemNodeFilter descentFilter);

}
