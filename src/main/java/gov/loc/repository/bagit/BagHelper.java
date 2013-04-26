
package gov.loc.repository.bagit;

import static com.google.common.base.Throwables.propagate;
import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.filesystem.DirNode;
import gov.loc.repository.bagit.filesystem.FileNode;
import gov.loc.repository.bagit.filesystem.FileSystemFactory;
import gov.loc.repository.bagit.filesystem.FileSystemFactory.UnsupportedFormatException;
import gov.loc.repository.bagit.impl.BagItTxtImpl;
import gov.loc.repository.bagit.impl.FileSystemBagFile;
import gov.loc.repository.bagit.utilities.FormatHelper.UnknownFormatException;
import gov.loc.repository.bagit.utilities.SizeHelper;
import gov.loc.repository.bagit.v0_95.impl.BagConstantsImpl;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BagHelper {

    private static final Logger log = LoggerFactory.getLogger(BagHelper.class);

    private static final String BAGIT = "bagit.txt";

    /*
     * Returns format or null if unable to determine format.
     */
    public static String getVersion(final File bagFile) {
        DirNode bagFileDirNode = null;
        try {
            bagFileDirNode = FileSystemFactory.getDirNodeForBag(bagFile);
        } catch (final UnknownFormatException e) {
            log.debug(MessageFormat
                    .format("Unable to determine version for {0} because unknown format.",
                            bagFile.toString()));
            return null;
        } catch (final UnsupportedFormatException e) {
            log.debug(MessageFormat
                    .format("Unable to determine version for {0} because unsupported format.",
                            bagFile.toString()));
        } catch (final IOException e) {
            propagate(e);
        }
        log.trace(MessageFormat.format(
                "BagFileDirNode has filepath {0} and is a {1}", bagFileDirNode
                        .getFilepath(), bagFileDirNode.getClass()
                        .getSimpleName()));

        final FileNode bagItNode = bagFileDirNode.childFile(BAGIT);
        if (bagItNode == null || !bagItNode.exists()) {
            log.debug(MessageFormat.format(
                    "Unable to determine version for {0}.", bagFile.toString()));
            return null;
        }
        final BagItTxt bagItTxt =
                new BagItTxtImpl(new FileSystemBagFile(BAGIT, bagItNode),
                        new BagConstantsImpl());
        log.debug(MessageFormat.format(
                "Determined that version for {0} is {1}.", bagFile.toString(),
                bagItTxt.getVersion()));
        return bagItTxt.getVersion();

    }

    public static long generatePayloadOctetCount(final Bag bag) {
        long count = 0;
        for (final BagFile bagFile : bag.getPayload()) {
            count = count + bagFile.getSize();
        }
        return count;
    }

    public static String generatePayloadOxum(final Bag bag) {
        return Long.toString(generatePayloadOctetCount(bag)) + "." +
                Long.toString(bag.getPayload().size());
    }

    public static long generateTagOctetCount(final Bag bag) {
        long count = 0;
        for (final BagFile bagFile : bag.getTags()) {
            count = count + bagFile.getSize();
        }
        return count;
    }

    public static String generateBagSize(final Bag bag) {
        final long count =
                generateTagOctetCount(bag) + generatePayloadOctetCount(bag);
        return SizeHelper.getSize(count);
    }

    public static boolean isPayload(String filepath,
            final BagConstants bagConstants) {
        filepath = FilenameUtils.normalize(filepath);
        return filepath.startsWith(bagConstants.getDataDirectory());
    }

}
