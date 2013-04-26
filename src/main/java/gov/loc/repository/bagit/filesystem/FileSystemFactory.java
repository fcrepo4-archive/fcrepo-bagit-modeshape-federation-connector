
package gov.loc.repository.bagit.filesystem;

import static gov.loc.repository.bagit.Bag.Format.FILESYSTEM;
import static gov.loc.repository.bagit.Bag.Format.ZIP;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.filesystem.impl.FileFileSystem;
import gov.loc.repository.bagit.filesystem.impl.ZipFileSystem;
import gov.loc.repository.bagit.utilities.FormatHelper;
import gov.loc.repository.bagit.utilities.FormatHelper.UnknownFormatException;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

public class FileSystemFactory {

    public static DirNode getDirNodeForBag(final File fileForBag)
            throws UnknownFormatException, UnsupportedFormatException,
            IOException {
        assert fileForBag != null;

        if (!fileForBag.exists()) {
            throw new RuntimeException(MessageFormat.format(
                    "{0} does not exist", fileForBag));
        }

        final Format format = FormatHelper.getFormat(fileForBag);
        FileSystem fs = null;
        if (FILESYSTEM.equals(format)) {
            fs = new FileFileSystem(fileForBag);
        } else if (ZIP.equals(format)) {
            fs = new ZipFileSystem(fileForBag);
        } else {
            throw new UnsupportedFormatException();
        }

        DirNode root = fs.getRoot();
        fs.close();
        if (format.isSerialized) {
            if (root.listChildren().size() != 1) {
                throw new RuntimeException(
                        "Unable to find bag_dir in serialized bag");
            }
            final FileSystemNode bagDirNode =
                    root.listChildren().iterator().next();
            if (!(bagDirNode instanceof DirNode)) {
                throw new RuntimeException(
                        "Unable to find bag_dir in serialized bag");
            }
            root = (DirNode) bagDirNode;
        }
        return root;
    }

    public static class UnsupportedFormatException extends Exception {

        private static final long serialVersionUID = 1L;

        public UnsupportedFormatException() {
            super("Unsupported format");
        }
    }

}
