
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

public class AddFilesToPayloadOperation extends LongRunningOperationBase {

    private static final Logger log = LoggerFactory
            .getLogger(AddFilesToPayloadOperation.class);

    private final Bag bag;

    public AddFilesToPayloadOperation(final Bag bag) {
        this.bag = bag;
    }

    public void addFilesToPayload(final List<File> files) {
        int count = 0;
        for (final File file : files) {
            if (this.isCancelled()) {
                return;
            }
            count = this.addPayload(file, file.getParentFile(), count);
        }
        return;
    }

    public void addFileToPayload(final File file) {
        this.addPayload(file, file.getParentFile(), 0);
    }

    private int addPayload(File file, final File rootDir, int count) {
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
                this.progress("Adding payload file to data directory",
                        filepath, count, null);
                log.trace(MessageFormat.format(
                        "Adding payload {0} in data directory", filepath));
                count = this.addPayload(child, rootDir, count);
            }

        } else if (file.isFile()) {

            //If file, add to payloadMap
            String filepath =
                    this.bag.getBagConstants().getDataDirectory() + "/";
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
                                .format("This Library does not support \\ in filepaths: {0}. See README.txt.",
                                        filepath));
            }
            count++;
            log.debug(MessageFormat.format("Adding {0} to payload.", filepath));
            this.bag.putBagFile(new FileBagFile(filepath, file));
        } else {
            throw new RuntimeException("Neither a directory or file");
        }
        return count;
    }

}
