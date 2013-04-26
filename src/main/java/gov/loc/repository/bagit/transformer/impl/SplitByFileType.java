
package gov.loc.repository.bagit.transformer.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.transformer.Splitter;

import java.util.ArrayList;
import java.util.List;

public class SplitByFileType implements Splitter {

    private final BagFactory bagFactory;

    private String[][] fileExtensions;

    private String[] exludeDirs;

    public String[][] getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(final String[][] fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public SplitByFileType(final BagFactory bagFactory,
            final String[][] fileExtensions, final String[] excludeDirs) {
        this.bagFactory = bagFactory;
        this.setFileExtensions(fileExtensions);
        this.setExludeDirs(excludeDirs);
    }

    @Override
    public List<Bag> split(final Bag srcBag) {
        final List<Bag> subBags = new ArrayList<Bag>();

        for (final String[] subFileExtension : this.fileExtensions) {
            //Sort out targeted bag files in the source bag
            final List<BagFile> targetedBagFiles = new ArrayList<BagFile>();
            for (final BagFile bagFile : srcBag.getPayload()) {
                final String fileExtension =
                        bagFile.getFilepath().substring(
                                bagFile.getFilepath().lastIndexOf('.') + 1);
                for (final String fileEx : subFileExtension) {
                    if (fileEx.trim().equalsIgnoreCase(fileExtension) &&
                            !SplitBagHelper.isExcluded(this.getExludeDirs(),
                                    bagFile.getFilepath())) {
                        targetedBagFiles.add(bagFile);
                        break;
                    }
                }
            }

            if (targetedBagFiles.size() > 0) {
                //Put the targeted bag files to a new bag
                final Bag subBag =
                        this.bagFactory.createBag(srcBag.getVersion());
                final BagInfoTxt bagInfoTxt =
                        subBag.getBagPartFactory().createBagInfoTxt();
                subBag.putBagFile(bagInfoTxt);
                //Add bag info from the source bag to the new bag
                subBag.getBagInfoTxt().putAll(srcBag.getBagInfoTxt());
                //Put file type info into bag-info.txt
                subBag.getBagInfoTxt().put(FILE_TYPE_KEY,
                        this.concatStrings(subFileExtension));

                subBag.putBagFiles(targetedBagFiles);

                subBags.add(subBag);
            }
        }

        return subBags;
    }

    private String concatStrings(final String[] strs) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        for (final String str : strs) {
            if (i > 0) {
                sb.append(" ").append(str);
            } else {
                sb.append(str);
            }
            i++;
        }
        return sb.toString();
    }

    public String[] getExludeDirs() {
        return exludeDirs;
    }

    public void setExludeDirs(final String[] exludeDirs) {
        this.exludeDirs = exludeDirs;
    }
}
