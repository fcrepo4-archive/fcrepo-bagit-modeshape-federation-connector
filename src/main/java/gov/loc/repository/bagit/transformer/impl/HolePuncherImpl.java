
package gov.loc.repository.bagit.transformer.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.impl.AbstractBagVisitor;
import gov.loc.repository.bagit.transformer.HolePuncher;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.utilities.SimpleResultHelper;
import gov.loc.repository.bagit.utilities.UrlHelper;
import gov.loc.repository.bagit.verify.FailModeSupporting.FailMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HolePuncherImpl extends AbstractBagVisitor implements HolePuncher {

    private static final Logger log = LoggerFactory
            .getLogger(HolePuncherImpl.class);

    private String baseUrl;

    private Bag originalBag;

    private Bag newBag;

    private FetchTxt fetch;

    private boolean includePayloadDirectory = false;

    private boolean includeTags = false;

    private final BagFactory bagFactory;

    private boolean resume = false;

    private boolean leaveTags = true;

    private SimpleResult bagVerifyResult;

    public HolePuncherImpl(final BagFactory bagFactory) {
        this.bagFactory = bagFactory;
    }

    @Override
    public Bag makeHoley(final Bag bag, final String baseUrl,
            final boolean includePayloadDirectoryInUrl,
            final boolean includeTags, final boolean resume) {
        return this.makeHoley(bag, baseUrl, includePayloadDirectoryInUrl,
                includeTags, true, resume);
    }

    @Override
    public Bag makeHoley(final Bag bag, final String baseUrl,
            final boolean includePayloadDirectoryInUrl,
            final boolean includeTags, final boolean leaveTags,
            final boolean resume) {
        log.info("Making bag holey");
        this.originalBag = bag;
        this.resume = resume;
        this.baseUrl = baseUrl;
        if (!this.baseUrl.endsWith("/")) {
            this.baseUrl += "/";
        }
        this.includePayloadDirectory = includePayloadDirectoryInUrl;
        this.includeTags = includeTags;
        if (includeTags) {
            this.includePayloadDirectory = true;
        }
        this.leaveTags = leaveTags;
        bag.accept(this);
        return this.newBag;
    }

    @Override
    public void startBag(final Bag bag) {
        this.newBag =
                this.bagFactory.createBag(bag.getBagConstants().getVersion());
        this.fetch = this.newBag.getBagPartFactory().createFetchTxt();
        this.newBag.putBagFile(this.fetch);
        this.originalBag.putBagFile(this.fetch);
        this.bagVerifyResult = this.originalBag.verifyValid(FailMode.FAIL_SLOW);
    }

    @Override
    public void visitPayload(final BagFile bagFile) {
        if (resume) {
            //Skip the file if the file is not missing or invalid. 
            if (!SimpleResultHelper.isMissingOrInvalid(bagVerifyResult, bagFile
                    .getFilepath())) {
                return;
            }
        }
        String url = baseUrl;
        if (includePayloadDirectory) {
            url += UrlHelper.encodeFilepath(bagFile.getFilepath());
        } else {
            url +=
                    UrlHelper.encodeFilepath(bagFile.getFilepath().substring(
                            this.newBag.getBagConstants().getDataDirectory()
                                    .length() + 1));
        }
        fetch.add(new FetchTxt.FilenameSizeUrl(bagFile.getFilepath(), bagFile
                .exists() ? bagFile.getSize() : null, url));
    }

    @Override
    public void visitTag(final BagFile bagFile) {
        if (resume) {
            //Skip the file if the file is not missing or invalid. 
            if (!SimpleResultHelper.isMissingOrInvalid(bagVerifyResult, bagFile
                    .getFilepath())) {
                return;
            }
        }
        if (includeTags) {
            final String url =
                    baseUrl + UrlHelper.encodeFilepath(bagFile.getFilepath());
            fetch.add(new FetchTxt.FilenameSizeUrl(bagFile.getFilepath(),
                    bagFile.exists() ? bagFile.getSize() : null, url));
        }
        if (!includeTags || leaveTags) {
            this.newBag.putBagFile(bagFile);
        }
    }
}
