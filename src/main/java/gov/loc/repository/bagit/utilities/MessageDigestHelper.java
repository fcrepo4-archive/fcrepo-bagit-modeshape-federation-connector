
package gov.loc.repository.bagit.utilities;

import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.MessageFormat;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDigestHelper {

    private static final Logger log = LoggerFactory
            .getLogger(MessageDigestHelper.class);

    private static final int BUFFERSIZE = 65536;

    public static String generateFixity(final File file,
            final Algorithm algorithm) {
        try {
            log.debug("Generating fixity for " + file.toString());
            return generateFixity(new FileInputStream(file), algorithm);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String generateFixity(final InputStream in,
            final Algorithm algorithm) {

        try {
            final MessageDigest md =
                    MessageDigest.getInstance(algorithm.javaSecurityAlgorithm);
            final byte[] dataBytes = new byte[BUFFERSIZE];
            int nread = in.read(dataBytes);
            while (nread > 0) {
                md.update(dataBytes, 0, nread);
                nread = in.read(dataBytes);
            }
            return new String(Hex.encodeHex(md.digest()));

        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(in);
        }

    }

    public static boolean fixityMatches(final InputStream in,
            final Algorithm algorithm, final String fixity) {
        if (fixity == null) {
            return false;
        }
        final String generatedFixity = generateFixity(in, algorithm);
        log.debug(MessageFormat.format(
                "Generated fixity is {0}.  Check fixity is {1}.",
                generatedFixity, fixity));
        if (generatedFixity.equalsIgnoreCase(fixity)) {
            return true;
        }
        return false;
    }

}
