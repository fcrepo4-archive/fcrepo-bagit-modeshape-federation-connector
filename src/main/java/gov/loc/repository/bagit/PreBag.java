
package gov.loc.repository.bagit;

import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.transformer.Completer;

import java.io.File;
import java.util.List;

public interface PreBag {

    void setFile(final File file);

    File getFile();

    void setTagFiles(final List<File> tagFiles);

    List<File> getTagFiles();

    void setIgnoreAdditionalDirectories(final List<String> dirs);

    Bag
            makeBagInPlace(final Version version,
                    final boolean retainBaseDirectory);

    Bag makeBagInPlace(final Version version,
            final boolean retainBaseDirectory,
            final boolean keepEmptyDirectories);

    Bag makeBagInPlace(final Version version,
            final boolean retainBaseDirectory,
            final boolean keepEmptyDirectories, final Completer completer);

    Bag makeBagInPlace(final Version version,
            final boolean retainBaseDirectory, final Completer completer);
}
