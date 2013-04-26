
package gov.loc.repository.bagit.filesystem.impl;

import gov.loc.repository.bagit.filesystem.DirNode;
import gov.loc.repository.bagit.filesystem.FileSystemNode;
import gov.loc.repository.bagit.filesystem.FileSystemNodeFilter;

import java.util.ArrayList;
import java.util.Collection;

public class NodeHelper {

    public static Collection<FileSystemNode> listDescendants(
            final DirNode baseNode, final FileSystemNodeFilter filter,
            final FileSystemNodeFilter descentFilter) {
        final Collection<FileSystemNode> fileSystemNodes =
                new ArrayList<FileSystemNode>();
        listDescendants(baseNode, filter, descentFilter, fileSystemNodes);
        return fileSystemNodes;
    }

    private static void listDescendants(final DirNode baseNode,
            final FileSystemNodeFilter filter,
            final FileSystemNodeFilter descentFilter,
            final Collection<FileSystemNode> fileSystemNodes) {
        for (final FileSystemNode child : baseNode.listChildren()) {
            if (filter == null || filter.accept(child)) {
                fileSystemNodes.add(child);
            }
            if (child instanceof DirNode &&
                    (descentFilter == null || descentFilter.accept(child))) {
                listDescendants((DirNode) child, filter, descentFilter,
                        fileSystemNodes);
            }
        }
    }

    public static Collection<FileSystemNode> listChildren(
            final DirNode baseNode, final FileSystemNodeFilter filter) {
        final Collection<FileSystemNode> fileSystemNodes =
                new ArrayList<FileSystemNode>();
        listChildren(baseNode, filter, fileSystemNodes);
        return fileSystemNodes;

    }

    private static void listChildren(final DirNode baseNode,
            final FileSystemNodeFilter filter,
            final Collection<FileSystemNode> fileSystemNodes) {
        for (final FileSystemNode child : baseNode.listChildren()) {
            if (filter == null || filter.accept(child)) {
                fileSystemNodes.add(child);
            }
        }
    }

}
