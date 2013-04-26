
package gov.loc.repository.bagit.utilities;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempFileHelper {

    private static final Logger log = LoggerFactory
            .getLogger(TempFileHelper.class);

    public static File getTempFile(final File file) {
        assert file != null;
        final File tempFile = new File(file.getPath() + ".biltemp");
        log.trace(MessageFormat.format("Temp file for {0} is {1}", file,
                tempFile));
        return tempFile;
    }

    public static void switchTemp(final File file) {
        final File tempFile = getTempFile(file);
        if (!tempFile.exists()) {
            final String msg =
                    MessageFormat.format(
                            "Temp file {0} for {1} does not exist.", tempFile,
                            file);
            log.error(msg);
            throw new RuntimeException(msg);
        }
        if (file.exists()) {
            try {
                FileUtils.forceDelete(file);
            } catch (final IOException ex) {
                final String msg =
                        MessageFormat.format("Error deleting {0}: {1}", file,
                                ex.getMessage());
                log.error(msg);
                throw new RuntimeException(msg, ex);
            }
        }
        try {
            FileUtils.moveFile(tempFile, file);
        } catch (final IOException ex) {
            final String msg =
                    MessageFormat.format("Error moving {0} to {1}: {2}",
                            tempFile, file, ex.getMessage());
            log.error(msg);
            throw new RuntimeException(msg, ex);
        }
    }
}
