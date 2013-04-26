
package gov.loc.repository.bagit;

import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManifestHelper {

    private static final Logger log = LoggerFactory
            .getLogger(ManifestHelper.class);

    public static boolean isPayloadManifest(final String filename,
            final BagConstants bagConstants) {
        if (filename.startsWith(bagConstants.getPayloadManifestPrefix()) &&
                filename.endsWith(bagConstants.getPayloadManifestSuffix())) {
            return true;
        }
        return false;
    }

    public static boolean isTagManifest(final String filename,
            final BagConstants bagConstants) {
        if (filename.startsWith(bagConstants.getTagManifestPrefix()) &&
                filename.endsWith(bagConstants.getTagManifestSuffix())) {
            return true;
        }
        return false;
    }

    public static Algorithm getAlgorithm(final String filename,
            final BagConstants bagConstants) {
        String bagItAlgorithm;
        if (isPayloadManifest(filename, bagConstants)) {
            bagItAlgorithm =
                    filename.substring(bagConstants.getPayloadManifestPrefix()
                            .length(), filename.length() -
                            bagConstants.getPayloadManifestSuffix().length());
        } else if (isTagManifest(filename, bagConstants)) {
            bagItAlgorithm =
                    filename.substring(bagConstants.getTagManifestPrefix()
                            .length(), filename.length() -
                            bagConstants.getTagManifestSuffix().length());
        } else {
            throw new RuntimeException(
                    "Algorithm not found in manifest filename");
        }
        final Algorithm algorithm =
                Algorithm.valueOfBagItAlgorithm(bagItAlgorithm);
        log.debug(MessageFormat.format(
                "Determined that algorithm for {0} is {1}.", filename,
                algorithm.toString()));
        return algorithm;

    }

    public static String getTagManifestFilename(final Algorithm algorithm,
            final BagConstants bagConstants) {
        return bagConstants.getTagManifestPrefix() + algorithm.bagItAlgorithm +
                bagConstants.getTagManifestSuffix();
    }

    public static String getPayloadManifestFilename(final Algorithm algorithm,
            final BagConstants bagConstants) {
        return bagConstants.getPayloadManifestPrefix() +
                algorithm.bagItAlgorithm +
                bagConstants.getPayloadManifestSuffix();
    }

}
