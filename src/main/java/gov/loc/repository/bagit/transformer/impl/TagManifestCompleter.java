
package gov.loc.repository.bagit.transformer.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.transformer.Completer;

import java.util.List;

public class TagManifestCompleter implements Completer {

    private final CompleterHelper helper;

    private final BagFactory bagFactory;

    private Algorithm tagManifestAlgorithm = Algorithm.MD5;

    private String nonDefaultManifestSeparator = null;

    private List<String> limitUpdateFilepaths = null;

    private List<String> limitDeleteFilepaths = null;

    private List<String> limitAddFilepaths = null;

    private List<String> limitUpdateDirectories = null;

    private List<String> limitDeleteDirectories = null;

    private List<String> limitAddDirectories = null;

    //Not bothering with extending LongRunningOperation since this should be fast
    //Not bothering with configuration of threadcount

    public TagManifestCompleter(final BagFactory bagFactory) {
        this.bagFactory = bagFactory;
        this.helper = new CompleterHelper();
    }

    public void setTagManifestAlgorithm(final Algorithm tagManifestAlgorithm) {
        this.tagManifestAlgorithm = tagManifestAlgorithm;
    }

    /*
     * Limit updates to the provided filepaths, i.e., only the manifest entries
     * of the
     * provided files will be updated.
     */
    public void setLimitUpdateTagFilepaths(final List<String> limitUpdateFiles) {
        this.limitUpdateFilepaths = limitUpdateFiles;
    }

    /*
     * Limit deletes to the provided filepaths, i.e., only the manifest entries
     * of the
     * provided files will be removed.
     */
    public void setLimitDeleteTagFilepaths(final List<String> limitDeleteFiles) {
        this.limitDeleteFilepaths = limitDeleteFiles;
    }

    /*
     * Limit additions to the provided filepaths, i.e., only manifest entries
     * for the
     * provided files will be added.
     */
    public void setLimitAddTagFilepaths(final List<String> limitAddFiles) {
        this.limitAddFilepaths = limitAddFiles;
    }

    /*
     * Limit updates to the files in and below the provided directories, i.e.,
     * only the manifest entries of files in or below the provided directories
     * will be updated.
     */
    public void setLimitUpdateTagDirectories(
            final List<String> limitUpdateDirectories) {
        this.limitUpdateDirectories = limitUpdateDirectories;
    }

    /*
     * Limit deletes to the files in and below the provided directories, i.e.,
     * only the manifest entries of files in or below the provided directories
     * will be removed.
     */
    public void setLimitDeleteTagDirectories(
            final List<String> limitDeleteDirectories) {
        this.limitDeleteDirectories = limitDeleteDirectories;
    }

    /*
     * Limit additions to the files in and below the provided directories, i.e.,
     * only manifest entries for files in or below the provided directories will
     * be added.
     */
    public void
            setLimitAddTagDirectories(final List<String> limitAddDirectories) {
        this.limitAddDirectories = limitAddDirectories;
    }

    @Override
    public Bag complete(final Bag bag) {

        final Bag newBag = this.bagFactory.createBag(bag);
        newBag.putBagFiles(bag.getPayload());
        newBag.putBagFiles(bag.getTags());

        //Delete anything that doesn't exist
        this.helper.cleanManifests(newBag, newBag.getTagManifests(),
                this.limitDeleteFilepaths, this.limitDeleteDirectories);

        //Regenerate the tag manifests
        for (final Manifest manifest : newBag.getTagManifests()) {
            this.helper.regenerateManifest(newBag, manifest, true,
                    this.limitUpdateFilepaths, this.limitUpdateDirectories);
        }
        //See if anything is missing
        this.helper.handleManifest(newBag, this.tagManifestAlgorithm,
                ManifestHelper.getTagManifestFilename(
                        this.tagManifestAlgorithm, newBag.getBagConstants()),
                newBag.getTags(), this.nonDefaultManifestSeparator,
                this.limitAddFilepaths, this.limitAddDirectories);
        return newBag;
    }

    public String getNonDefaultManifestSeparator() {
        return this.nonDefaultManifestSeparator;
    }

    public void setNonDefaultManifestSeparator(final String manifestSeparator) {
        this.nonDefaultManifestSeparator = manifestSeparator;
    }

}
