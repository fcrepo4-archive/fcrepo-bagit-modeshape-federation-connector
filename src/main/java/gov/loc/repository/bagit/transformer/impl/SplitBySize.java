
package gov.loc.repository.bagit.transformer.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.transformer.Splitter;
import gov.loc.repository.bagit.utilities.SizeHelper;
import gov.loc.repository.bagit.utilities.namevalue.NameValueReader.NameValue;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SplitBySize implements Splitter {

    private Double maxBagSize;

    private boolean keepLowestLevelDir;

    private final BagFactory bagFactory;

    private String[] exludeDirs;

    public SplitBySize(final BagFactory bagFactory, final Double maxBagSize,
            final boolean keepLowestLevelDir, final String[] excludeDirs) {
        this.bagFactory = bagFactory;
        this.setKeepLowestLevelDir(keepLowestLevelDir);
        this.setMaxBagSize(maxBagSize);
        this.setExludeDirs(excludeDirs);
    }

    @Override
    public List<Bag> split(final Bag srcBag) {
        final List<Bag> subBags = new ArrayList<Bag>();

        //Sort bag files in the source bag
        final List<BagFile> sortedBagFiles =
                this.sortBagFiles(srcBag.getPayload(), this
                        .isKeepLowestLevelDir(), this.getMaxBagSize(), this
                        .getExludeDirs());

        //Group bag files of the source bag
        final List<BagFileGroup> bagFileGroups =
                group(sortedBagFiles, this.getMaxBagSize());

        //Put each group of bag files to a separate new bag
        for (final BagFileGroup bagFileGroup : bagFileGroups) {
            final List<BagFile> groupBagFiles = bagFileGroup.getBagFiles();
            final Bag subBag = bagFactory.createBag(srcBag.getVersion());

            if (srcBag.getBagInfoTxt() != null) {
                final BagInfoTxt bagInfoTxt =
                        subBag.getBagPartFactory().createBagInfoTxt();
                subBag.putBagFile(bagInfoTxt);
                //Add bag info from the source bag to the split bag
                final List<NameValue> list = srcBag.getBagInfoTxt().asList();
                for (final NameValue nameValue : list) {
                    subBag.getBagInfoTxt().put(nameValue);
                }
            }

            for (final BagFile bagFile : groupBagFiles) {
                if (bagFile instanceof LowestLevelBagDir) {
                    subBag.putBagFiles(((LowestLevelBagDir) bagFile)
                            .getBagFiles());
                } else {
                    subBag.putBagFile(bagFile);
                }
            }

            subBags.add(subBag);
        }
        return subBags;
    }

    private List<BagFile> sortBagFiles(
            final Collection<BagFile> payloadBagFiles,
            final boolean keepLowestLevelDir, final Double maxBagSize,
            final String[] excludeDirs) {
        final List<BagFile> sortedBagFiles = new ArrayList<BagFile>();

        //Get all the file path directories 
        final Set<String> filePathDirs = new HashSet<String>();
        for (final BagFile bagFile : payloadBagFiles) {
            if (!SplitBagHelper.isExcluded(excludeDirs, this
                    .getFilePathDir(bagFile.getFilepath()))) {
                filePathDirs.add(this.getFilePathDir(bagFile.getFilepath()));
            }
        }

        if (keepLowestLevelDir) {
            for (final String filePathdir : filePathDirs) {
                //If a lowest level directory, group all bag files under the directory into a single LowestLevelBagDir object.  Add the single object to the result list.
                if (isLowestLevelDir(filePathdir, filePathDirs)) {
                    final LowestLevelBagDir lowestLevelBagDir =
                            new LowestLevelBagDir(filePathdir);
                    for (final BagFile bagFile : payloadBagFiles) {
                        if (this.getFilePathDir(bagFile.getFilepath()).equals(
                                filePathdir)) {
                            lowestLevelBagDir.addBagFile(bagFile);
                        }
                    }

                    if (lowestLevelBagDir.getSize() >= maxBagSize) {
                        throw new RuntimeException(
                                MessageFormat
                                        .format("The size of the lowest level directory {0} exceeds the maximum split bag size {1}.",
                                                lowestLevelBagDir.getFilepath(),
                                                SizeHelper.getSize(maxBagSize
                                                        .longValue())));
                    }

                    sortedBagFiles.add(lowestLevelBagDir);
                }
                //Otherwise, add all the bag files under the directory to the result list.
                else {
                    for (final BagFile bagFile : payloadBagFiles) {
                        if (this.getFilePathDir(bagFile.getFilepath()).equals(
                                filePathdir)) {
                            if (bagFile.getSize() >= maxBagSize) {
                                throw new RuntimeException(
                                        MessageFormat
                                                .format("The size of the file {0} exceeds the maximum split bag size {1}.",
                                                        bagFile.getFilepath(),
                                                        SizeHelper
                                                                .getSize((maxBagSize
                                                                        .longValue()))));
                            }
                            if (!SplitBagHelper.isExcluded(excludeDirs, bagFile
                                    .getFilepath())) {
                                sortedBagFiles.add(bagFile);
                            }
                        }
                    }
                }
            }
        } else {
            for (final BagFile bagFile : payloadBagFiles) {
                if (bagFile.getSize() >= maxBagSize) {
                    throw new RuntimeException(
                            MessageFormat
                                    .format("The size of the file {0} exceeds the maximum split bag size {1}.",
                                            bagFile.getFilepath(), SizeHelper
                                                    .getSize((maxBagSize
                                                            .longValue()))));
                }
                if (!SplitBagHelper.isExcluded(excludeDirs, bagFile
                        .getFilepath())) {
                    sortedBagFiles.add(bagFile);
                }
            }
        }

        return sortedBagFiles;
    }

    private String getFilePathDir(final String filePath) {
        return filePath.substring(0, filePath.lastIndexOf('/'));
    }

    private boolean isLowestLevelDir(final String filePathDir,
            final Set<String> filePathDirs) {
        for (final String filePathDirItem : filePathDirs) {
            if (filePathDirItem.equals(filePathDir)) {
                continue;
            }
            if (filePathDirItem.indexOf(filePathDir) >= 0) {
                return false;
            }
        }
        return true;
    }

    private List<BagFileGroup> group(final List<BagFile> bagFiles,
            final Double maxBagSize) {

        //Sort bag files by size in descending order
        Collections.sort(bagFiles, new BagFileSizeReverseComparator());

        final List<BagFileGroup> bagFileGroups = new ArrayList<BagFileGroup>();
        for (final BagFile bagFile : bagFiles) {
            if (bagFileGroups.isEmpty()) {
                final BagFileGroup group = new BagFileGroup(maxBagSize);
                group.addBagFile(bagFile);
                bagFileGroups.add(group);
            } else {
                boolean foundSpace = false;

                //Put the bag file in the first group which has enough space for the bag file 
                for (final BagFileGroup bagFileGroup : bagFileGroups) {
                    if (bagFileGroup.hasSpace(bagFile)) {
                        bagFileGroup.addBagFile(bagFile);
                        foundSpace = true;
                        break;
                    }
                }

                //If the bag file does not find a group, put it in a new group
                if (!foundSpace) {
                    final BagFileGroup group = new BagFileGroup(maxBagSize);
                    group.addBagFile(bagFile);
                    bagFileGroups.add(group);
                }
            }
        }

        return bagFileGroups;
    }

    private class BagFileSizeReverseComparator implements Comparator<BagFile> {

        @Override
        public int compare(final BagFile bagFile1, final BagFile bagFile2) {
            return new Long(bagFile2.getSize()).compareTo(new Long(bagFile1
                    .getSize()));
        }

    }

    private class BagFileGroup {

        List<BagFile> bagFiles = new ArrayList<BagFile>();

        Double groupSize = 0.0;

        Double maxGroupSize = 300 * SizeHelper.GB;

        public BagFileGroup(final Double maxGroupSize) {
            this.maxGroupSize = maxGroupSize;
        }

        public List<BagFile> getBagFiles() {
            return bagFiles;
        }

        public boolean hasSpace(final BagFile bagFile) {
            if (groupSize + bagFile.getSize() > maxGroupSize) {
                return false;
            }
            return true;
        }

        public void addBagFile(final BagFile bagFile) {
            this.bagFiles.add(bagFile);
            this.groupSize += bagFile.getSize();
        }
    }

    private class LowestLevelBagDir implements BagFile {

        private final String filePath;

        private final List<BagFile> bagFiles = new ArrayList<BagFile>();

        public LowestLevelBagDir(final String filePath) {
            this.filePath = filePath;
        }

        public List<BagFile> getBagFiles() {
            return this.bagFiles;
        }

        @Override
        public boolean exists() {
            throw new RuntimeException("Operation not supported exception.");
        }

        @Override
        public String getFilepath() {
            return this.filePath;
        }

        @Override
        public long getSize() {
            long length = 0L;
            for (final BagFile bagFile : bagFiles) {
                length += bagFile.getSize();
            }
            return length;
        }

        @Override
        public InputStream newInputStream() {
            throw new RuntimeException("Operation not supported exception.");
        }

        public void addBagFile(final BagFile bagFile) {
            this.bagFiles.add(bagFile);
        }
    }

    public Double getMaxBagSize() {
        return maxBagSize;
    }

    public void setMaxBagSize(final Double maxBagSize) {
        this.maxBagSize = maxBagSize;
    }

    public boolean isKeepLowestLevelDir() {
        return keepLowestLevelDir;
    }

    public void setKeepLowestLevelDir(final boolean keepLowestLevelDir) {
        this.keepLowestLevelDir = keepLowestLevelDir;
    }

    public String[] getExludeDirs() {
        return exludeDirs;
    }

    public void setExludeDirs(final String[] exludeDirs) {
        this.exludeDirs = exludeDirs;
    }
}
