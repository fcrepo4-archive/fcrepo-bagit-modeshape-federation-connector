
package gov.loc.repository.bagit.utilities;

import java.io.File;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {

    private static final Logger log = LoggerFactory.getLogger(FileHelper.class);

    public static File normalizeForm(final File file) {
        if (file == null) {
            return file;
        }
        if (file.exists()) {
            log.debug(MessageFormat.format(
                    "No problem with form of filename for {0}", file));
            return file;
        }
        final File nfcFile =
                new File(Normalizer.normalize(file.getAbsolutePath(), Form.NFC));
        if (nfcFile.exists()) {
            log.debug(MessageFormat.format(
                    "Using NFC form of filename for {0}", file));
            return nfcFile;
        }
        final File nfdFile =
                new File(Normalizer.normalize(file.getAbsolutePath(), Form.NFD));
        if (nfdFile.exists()) {
            log.debug(MessageFormat.format(
                    "Using NFD form of filename for {0}", file));
            return nfdFile;
        }
        return file;
    }

    public static File[] normalizeForm(final File[] files) {
        for (int i = 0; i < files.length; i++) {
            files[i] = normalizeForm(files[i]);
        }
        return files;
    }
}
