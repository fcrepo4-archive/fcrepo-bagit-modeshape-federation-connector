
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.utilities.FileHelper;
import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagit.utilities.LongRunningOperationBase;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddFilesToTagsOperation extends LongRunningOperationBase {

    private static final Logger log = LoggerFactory
            .getLogger(AddFilesToTagsOperation.class);

    private final Bag bag;

    public AddFilesToTagsOperation(final Bag bag) {
        this.bag = bag;
    }

    public void addFilesToTags(final List<File> files) {
        int count = 0;
        for (final File file : files) {
            if (this.isCancelled()) {
                return;
            }
            count = this.addTag(file, file.getParentFile(), count);
        }
        return;
    }

    public void addFileToTags(final File file) {
        this.addTag(file, file.getParentFile(), 0);
    }

    private int addTag(File file, final File rootDir, int count) {
        if (this.isCancelled()) {
            return 0;
        }
        file = FileHelper.normalizeForm(file);
        if (!file.canRead()) {
            throw new RuntimeException("Can't read " + file.toString());
        }
        //If directory, recurse on children
        if (file.isDirectory()) {
            for (final File child : file.listFiles()) {
                if (this.isCancelled()) {
                    return 0;
                }
                final String filepath = file.getAbsolutePath();
                this.progress("Adding tag file", filepath, count, null);
                log.trace(MessageFormat.format("Adding tag {0}", filepath));
                count = this.addTag(child, rootDir, count);
            }

        } else if (file.isFile()) {

            //If file, add to payloadMap
            String filepath = "";
            if (rootDir != null) {
                filepath +=
                        FilenameHelper.removeBasePath(rootDir.toString(), file
                                .toString());
            } else {
                filepath += file.toString();
            }
            if (filepath.indexOf('\\') != -1) {
                throw new UnsupportedOperationException(
                        MessageFormat
                                .format("This library does not support \\ in filepaths: {0}. See README.txt.",
                                        filepath));
            }
            count++;
            log.debug(MessageFormat.format("Adding {0} to tags.", filepath));
            this.bag.putBagFile(new FileBagFile(filepath, file));
        } else {
            throw new RuntimeException("Neither a directory nor file");
        }
        return count;
    }

}
