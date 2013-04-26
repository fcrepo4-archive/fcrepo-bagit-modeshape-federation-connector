
package gov.loc.repository.bagit.utilities;

import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilenameHelper {

    private static final Logger log = LoggerFactory
            .getLogger(FilenameHelper.class);

    public static String normalizePathSeparators(final String filename) {
        if (filename == null) {
            return null;
        }
        final String newFilename = FilenameUtils.separatorsToUnix(filename);
        log.trace(MessageFormat.format("Normalized {0} to {1}", filename,
                newFilename));
        return newFilename;
    }

    public static String removeBasePath(final String basePath,
            final String filename) {
        if (filename == null) {
            throw new RuntimeException("Cannot remove basePath from null");
        }
        final String normBasePath = normalizePathSeparators(basePath);
        final String normFilename = normalizePathSeparators(filename);
        String filenameWithoutBasePath = null;
        if (basePath == null || basePath.length() == 0) {
            filenameWithoutBasePath = normFilename;
        } else {
            if (!normFilename.startsWith(normBasePath)) {
                throw new RuntimeException(MessageFormat.format(
                        "Cannot remove basePath {0} from {1}", basePath,
                        filename));
            }
            if (normBasePath.equals(normFilename)) {
                filenameWithoutBasePath = "";
            } else if (normBasePath.endsWith("/")) {
                filenameWithoutBasePath =
                        normFilename.substring(normBasePath.length());
            } else {
                filenameWithoutBasePath =
                        normFilename.substring(normBasePath.length() + 1);
            }
        }
        log.trace(MessageFormat
                .format("Removing {0} (normalized to {1}) from {2} (normalized to {3}) resulted in {4}",
                        basePath, normBasePath, filename, normFilename,
                        filenameWithoutBasePath));
        return filenameWithoutBasePath;
    }

    /**
     * Normalizes a file path by replacing various special
     * path tokens (., .., etc.) with their canonical equivalents.
     * @param filepath The file path to normalize.
     * @return The normalized file path.
     */
    public static String normalizePath(String filepath) {
        if (filepath.startsWith("./") || filepath.startsWith(".\\")) {
            filepath = filepath.substring(2);
        }
        filepath = filepath.replace("/./", "/");
        filepath = filepath.replace("\\.\\", "\\");
        int endPos = filepath.indexOf("/../");
        while (endPos != -1) {
            int startPos = endPos - 1;
            while (startPos >= 0 && '/' != filepath.charAt(startPos)) {
                startPos--;
            }
            if (startPos > 0) {
                filepath =
                        filepath.substring(0, startPos) + "/" +
                                filepath.substring(endPos + 4);
            } else {
                filepath = filepath.substring(endPos + 4);
            }
            endPos = filepath.indexOf("/../");
        }
        endPos = filepath.indexOf("\\..\\");
        while (endPos != -1) {
            int startPos = endPos - 1;
            while (startPos >= 0 && '\\' != filepath.charAt(startPos)) {
                startPos--;
            }
            if (startPos > 0) {
                filepath =
                        filepath.substring(0, startPos) + "\\" +
                                filepath.substring(endPos + 4);
            } else {
                filepath = filepath.substring(endPos + 4);
            }
            endPos = filepath.indexOf("\\..\\");
        }
        return filepath;
    }

    public static String getName(final String filepath) {
        final String name = FilenameUtils.getName(filepath);
        log.trace(MessageFormat.format("Name extracted from {0} is {1}",
                filepath, name));
        return name;
    }

    public static String concatFilepath(final String basepath,
            final String filenameToAdd) {
        final String filepath =
                normalizePathSeparators(FilenameUtils.concat(basepath,
                        filenameToAdd));
        log.trace(MessageFormat.format("Concatenation of {0} and {1} is {2}",
                basepath, filenameToAdd, filepath));
        return filepath;
    }

}
