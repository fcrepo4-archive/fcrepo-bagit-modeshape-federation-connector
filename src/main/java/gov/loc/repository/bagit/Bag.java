
package gov.loc.repository.bagit;

import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.transformer.HolePuncher;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.FailModeSupporting.FailMode;
import gov.loc.repository.bagit.verify.Verifier;
import gov.loc.repository.bagit.writer.Writer;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the core interface of the BagIt Library, representing
 * a bag from the BagIt spec.
 * Methods are available for creating, manipulating, writing, validating,
 * and verifying bags.</p>
 * 
 * <p>You should not create a Bag instance directly.  Instead, use an
 * appropriate method on the {@link BagFactory} class.</p>
 *
 * @see BagFactory
 */
public interface Bag extends Closeable {

    /**
     * <p>The format of a bag.  Bags may be serialized (such
     * as "zip") or they may simply be directories on
     * the filesystem (such as "file").</p>
     * 
     * <table border="2">
     * <tbody>
     * <tr><th>Format</th><th>Scheme</th><th>Extension</th><th>Serialized?</th></tr>
     * <tr><td>{@link #FILESYSTEM}</td><td>file</td><td>&lt;none&gt;</td><td>false</td></tr>
     * <tr><td>{@link #ZIP}</td><td>zip</td><td>.zip</td><td>true</td></tr>
     * </tbody>
     * </table>
     */
    enum Format {
        ZIP("zip", true, ".zip"), FILESYSTEM("file", false, "");

        /**
         * The URI scheme for the format.
         */
        public String scheme;

        /**
         * Whether or not the format is a serialized bag format.
         */
        public boolean isSerialized;

        /**
         * The file extension typicaly appended to a bag name
         * in the given format when it is written to disk.
         */
        public String extension;

        Format(final String scheme, final boolean isSerialized,
                final String extension) {
            this.scheme = scheme;
            this.isSerialized = isSerialized;
            this.extension = extension;
        }

    };

    /**
     * Gets the version of the BagIt spec to which the bag conforms.
     * @return The version of the bag.  Will never be null.
     */
    Version getVersion();

    File getFile();

    void setFile(final File file);

    List<Manifest> getPayloadManifests();

    Manifest getPayloadManifest(final Algorithm algorithm);

    List<Manifest> getTagManifests();

    Manifest getTagManifest(final Algorithm algorithm);

    Collection<BagFile> getTags();

    Collection<BagFile> getPayload();

    void removeBagFile(final String filepath);

    void removeTagDirectory(final String filepath);

    void removePayloadDirectory(final String filepath);

    BagFile getBagFile(final String filepath);

    void putBagFile(final BagFile bagFile);

    void putBagFiles(final Collection<BagFile> bagFiles);

    void addFileToPayload(final File file);

    void addFilesToPayload(final List<File> files);

    void addFileAsTag(final File file);

    void addFilesAsTag(final List<File> files);

    /**
     * Finds checksums in all manifests for a file.
     */
    Map<Algorithm, String> getChecksums(final String filepath);

    BagItTxt getBagItTxt();

    BagInfoTxt getBagInfoTxt();

    FetchTxt getFetchTxt();

    Format getFormat();

    /**
     * Determines whether the bag is valid according to the BagIt Specification.
     */
    SimpleResult verifyValid();

    SimpleResult verifyValid(final FailMode failMode);

    /**
     * Determines whether the bag is complete according to the BagIt Specification.
     */
    SimpleResult verifyComplete();

    SimpleResult verifyComplete(final FailMode failMode);

    /**
     * Invokes a Verifier to verify a bag.
     */
    SimpleResult verify(final Verifier verifier);

    /**
     * Verify that each checksum in every payload manifest can be verified against
     * the appropriate contents.
     */
    SimpleResult verifyPayloadManifests();

    SimpleResult verifyPayloadManifests(final FailMode failMode);

    /**
     * Verify that each checksum in every tag manifest can be verified against
     * the appropriate contents.
     */
    SimpleResult verifyTagManifests();

    SimpleResult verifyTagManifests(final FailMode failMode);

    /**
     * Loads a bag based on the tag files found on disk and the payload files listed in the payload manifests.
     */
    void loadFromManifests();

    /**
     * Loads a bag based on the tag files and payload files found on disk.
     */
    void loadFromFiles();

    void loadFromFiles(final List<String> ignoreAdditionalDirectories);

    /**
     * Invokes a BagVisitor.
     */
    void accept(final BagVisitor visitor);

    Bag write(final Writer writer, final File file);

    /**
     * Makes a bag holey by creating a fetch.txt and removing payload files.
     */

    Bag makeHoley(final String baseUrl,
            final boolean includePayloadDirectoryInUrl,
            final boolean includeTags, final boolean resume);

    /**
     * Invokes a HolePuncher to make a bag holey.
     */
    Bag makeHoley(final HolePuncher holePuncher, final String baseUrl,
            final boolean includePayloadDirectoryInUrl,
            final boolean includeTags, final boolean resume);

    /**
     * Makes a bag complete by filling in any pieces necessary to satisfy the BagIt Specification.
     */
    Bag makeComplete();

    /**
     * Invokes a Completer to make a bag complete.
     */
    Bag makeComplete(final Completer completer);

    BagConstants getBagConstants();

    BagPartFactory getBagPartFactory();

    /**
     * <p>Contains names for constants associated with a bag.
     * BagIt defines and reserves several names, and some of those names
     * change between versions of the specification.  This interface
     * abstracts away those constants so they can be examined on a
     * per-version basis.</p>
     * 
     * <p>For example, the <c>bag-info.txt</c> file was called
     * <c>package-info.txt</c> in earlier versions of the spec.
     * The correct name can be determined by using the
     * {@link #getBagInfoTxt()} method.</p>
     * 
     * <p>You should never reference BagIt constants by name directly
     * in your code, as they may change from version to version.  Instead,
     * obtain an instance of this interface and its values as the
     * constants.
     * Constants for the current bag's version may be obtained by
     * calling the {@link Bag#getBagConstants()} method.
     * Constants for a particular BagIt version may be obtained
     * by calling the {@link BagFactory#getBagConstants(Version)}
     * method.</p>
     * 
     * @see Bag#getBagConstants()
     * @see BagFactory#getBagConstants()
     * @see BagFactory#getBagConstants(Version)
     */
    public interface BagConstants {

        /**
         * Get the prefix for a payload manifest, "manifest-"
         * in the latest version.
         * @return The constant.
         */
        String getPayloadManifestPrefix();

        /**
         * Get the prefix for a payload manifest, "tagmanifest-"
         * in the latest version.
         * @return The constant.
         */
        String getTagManifestPrefix();

        /**
         * Get the prefix for a payload manifest, ".txt"
         * in the latest version.
         * @return The constant.
         */
        String getPayloadManifestSuffix();

        /**
         * Get the prefix for a payload manifest, ".txt"
         * in the latest version.
         * @return The constant.
         */
        String getTagManifestSuffix();

        /**
         * Get the text encoding required for the
         * {@link #getBagItTxt() bagit.txt} file, "UTF-8" in the latest
         * version.
         * @return The constant.
         */
        String getBagEncoding();

        /**
         * Get the name of the bag declaration file, "bagit.txt"
         * in the latest version.
         * @return The constant.
         */
        String getBagItTxt();

        /**
         * Get the name of the payload directory, "data"
         * in the latest version.
         * @return The constant.
         */
        String getDataDirectory();

        /**
         * Get the name of the standard bag metdata file, "bag-info.txt"
         * in the latest version.
         * @return The constant.
         */
        String getBagInfoTxt();

        /**
         * Get the name of the fetch file, "fetch.txt"
         * in the latest version.
         * @return The constant.
         */
        String getFetchTxt();

        /**
         * Get the version of the spec these constants are for.
         * @return The version.
         */
        Version getVersion();
    }

    /**
     * <p>Creates various parts of a bag, as appropriate for the
     * version and underlying implementation of the {@link Bag} interface.</p>
     * 
     * <p>You should never create implementations for the various components
     * of a bag directly.  Instead, you should obtain a parts factory through
     * {@link Bag#getBagPartFactory()} and then create the desired component
     * through the factory.</p>
     * 
     * <p>The components created are not already "bound" to a bag.  They
     * must still be added to the bag using methods such as
     * {@link Bag#putBagFile(BagFile)}.</p>
     * 
     * @see Bag
     */
    public interface BagPartFactory {

        ManifestReader createManifestReader(final InputStream in,
                final String encoding);

        ManifestReader createManifestReader(final InputStream in,
                final String encoding,
                final boolean treatBackSlashAsPathSeparator);

        ManifestWriter createManifestWriter(final OutputStream out);

        ManifestWriter createManifestWriter(final OutputStream out,
                final String manifestSeparator);

        Manifest createManifest(final String name);

        Manifest createManifest(final String name, final BagFile sourceBagFile);

        BagItTxtReader createBagItTxtReader(final String encoding,
                final InputStream in);

        BagItTxtWriter createBagItTxtWriter(final OutputStream out,
                final String encoding, final int lineLength,
                final int indentSpaces);

        BagItTxtWriter createBagItTxtWriter(final OutputStream out,
                final String encoding);

        BagItTxt createBagItTxt(final BagFile bagFile);

        BagItTxt createBagItTxt();

        BagInfoTxtReader createBagInfoTxtReader(final String encoding,
                final InputStream in);

        BagInfoTxtWriter createBagInfoTxtWriter(final OutputStream out,
                final String encoding, final int lineLength,
                final int indentSpaces);

        BagInfoTxtWriter createBagInfoTxtWriter(final OutputStream out,
                final String encoding);

        BagInfoTxt createBagInfoTxt(final BagFile bagFile);

        BagInfoTxt createBagInfoTxt();

        FetchTxtReader createFetchTxtReader(final InputStream in,
                final String encoding);

        FetchTxtWriter createFetchTxtWriter(final OutputStream out);

        FetchTxt createFetchTxt();

        FetchTxt createFetchTxt(final BagFile sourceBagFile);

        Version getVersion();
    }

}