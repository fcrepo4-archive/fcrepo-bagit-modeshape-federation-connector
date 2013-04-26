
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagVisitor;
import gov.loc.repository.bagit.Cancellable;

public abstract class AbstractBagVisitor implements BagVisitor, Cancellable {

    private boolean isCancelled = false;

    @Override
    public void cancel() {
        this.isCancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void endBag() {
    }

    @Override
    public void endPayload() {
    }

    @Override
    public void endTags() {
    }

    @Override
    public void startBag(final Bag bag) {
    }

    @Override
    public void startPayload() {
    }

    @Override
    public void startTags() {
    }

    @Override
    public void visitPayload(final BagFile bagFile) {
    }

    @Override
    public void visitTag(final BagFile bagFile) {
    }

}
