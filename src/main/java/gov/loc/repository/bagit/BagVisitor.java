
package gov.loc.repository.bagit;

public interface BagVisitor {

    public void startBag(final Bag bag);

    public void startTags();

    public void visitTag(final BagFile bagFile);

    public void endTags();

    public void startPayload();

    public void visitPayload(final BagFile bagFile);

    public void endPayload();

    public void endBag();

}
