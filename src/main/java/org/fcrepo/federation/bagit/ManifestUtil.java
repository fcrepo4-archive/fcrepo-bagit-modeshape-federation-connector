
package org.fcrepo.federation.bagit;

import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.google.common.base.Function;

/**
 * Includes helper methods and functions related to BagIt manifest files.
 * @author Gregory Jansen
 *
 */
public class ManifestUtil {
    private static final Logger logger = getLogger(ManifestUtil.class);

	static final Pattern MANIFEST = compile("^manifest-([^\\.]+).txt$");

	static final Pattern TAG_MANIFEST = compile("^tagmanifest-([^\\.]+).txt$");

	static GetFilesFromManifest getFilesFromManifest =
			new GetFilesFromManifest();

	static boolean isManifest(final String fileName) {
		final Matcher m = MANIFEST.matcher(fileName);
		if (m.find()) {
			final String csa = m.group(1);
			try {
				MessageDigest.getInstance(csa);
				return true;
			} catch (final NoSuchAlgorithmException e) {
				logger.warn(
						"Ignoring potential manifest file {} because {} is not a supported checksum algorithm.",
						fileName, csa);
			}
		}
		return false;
	}

	static boolean isManifest(final File file) {
		if (file.isFile() && file.canRead() && !file.isHidden()) {
			return (isManifest(file.getName()));
		} else {
			return false;
		}
	}

	static boolean isManifest(final Path path) {
		return isManifest(path.getFileName().toString());
	}

	static boolean isTagManifest(final String fileName) {
		final Matcher m = TAG_MANIFEST.matcher(fileName);
		if (m.find()) {
			final String csa = m.group(1);
			try {
				MessageDigest.getInstance(csa);
				return true;
			} catch (final NoSuchAlgorithmException e) {
				logger.warn(
						"Ignoring potential tag-manifest file {} because {} is not a supported checksum algorithm.",
						fileName, csa);
			}
		}
		return false;
	}

	static boolean isTagManifest(final File file) {
		if (file.isFile() && file.canRead() && !file.isHidden()) {
			return isTagManifest(file.getName());
		} else {
			return false;
		}
	}

	static boolean isTagManifest(final Path path) {
		return isManifest(path.getFileName().toString());
	}

	static class GetFilesFromManifest implements
			Function<File, Collection<File>> {

		@Override
		public Collection<File> apply(final File input) {
			try (final LineNumberReader lnr =
					new LineNumberReader(new FileReader(input))) {
				final ArrayList<File> result = new ArrayList<File>();
				String line;
				while ((line = lnr.readLine()) != null) {
					final String fileName = line.split(" ")[0];
					final File file = new File(input.getParentFile(), fileName);
					result.add(file);
				}
				return result;
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
