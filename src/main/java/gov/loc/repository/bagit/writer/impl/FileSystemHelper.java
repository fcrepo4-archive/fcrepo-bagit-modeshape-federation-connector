
package gov.loc.repository.bagit.writer.impl;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.TempFileHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemHelper {

    private static final Logger log = LoggerFactory
            .getLogger(FileSystemHelper.class);

    private static final int BUFFERSIZE = 65536;

    public static void write(final BagFile bagFile, final File file) {
        final File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            try {
                FileUtils.forceMkdir(parentDir);
            } catch (final IOException ex) {
                final String msg =
                        MessageFormat.format("Error creating {0}: {1}",
                                parentDir, ex.getMessage());
                log.error(msg);
                throw new RuntimeException(msg, ex);
            }
        }

        final File tempFile = TempFileHelper.getTempFile(file);
        FileOutputStream out;
        try {
            out = new FileOutputStream(tempFile);
        } catch (final FileNotFoundException ex) {
            final String msg =
                    MessageFormat.format("Error opening {0} for writing: {1}",
                            tempFile, ex.getMessage(), ex);
            log.error(msg);
            throw new RuntimeException(msg, ex);
        }
        final InputStream in = bagFile.newInputStream();
        try {
            final byte[] dataBytes = new byte[BUFFERSIZE];
            int nread = in.read(dataBytes);
            while (nread > 0) {
                out.write(dataBytes, 0, nread);
                nread = in.read(dataBytes);
            }
        } catch (final Exception ex) {
            final String msg =
                    MessageFormat.format(
                            "Error writing {0} to temp file {1}: {2}", bagFile
                                    .getFilepath(), tempFile, ex.getMessage());
            log.error(msg);
            throw new RuntimeException(msg, ex);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
        TempFileHelper.switchTemp(file);

    }

    public static void copy(final File sourceFile, final File file) {
        if (sourceFile.equals(file)) {
            throw new RuntimeException(MessageFormat.format(
                    "Cannot copy {0} to itself", sourceFile));
        }
        try {
            FileUtils.copyFile(sourceFile, file, true);
        } catch (final IOException e) {
            throw new RuntimeException(MessageFormat.format(
                    "Error copying {0} to {1}: {2}", sourceFile, file, e
                            .getMessage()), e);
        }
    }

    public static void move(final File sourceFile, final File file) {
        if (sourceFile.equals(file)) {
            throw new RuntimeException(MessageFormat.format(
                    "Cannot move {0} to itself", sourceFile));
        }
        try {
            FileUtils.moveFile(sourceFile, file);
        } catch (final IOException e) {
            throw new RuntimeException(MessageFormat.format(
                    "Error copying {0} to {1}: {2}", sourceFile, file, e
                            .getMessage()), e);
        }
    }

}
