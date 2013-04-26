
package gov.loc.repository.bagit.writer.impl;

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.impl.AbstractBagVisitor;
import gov.loc.repository.bagit.utilities.TempFileHelper;
import gov.loc.repository.bagit.writer.Writer;

import java.io.File;
import java.util.ArrayList;

public abstract class AbstractWriter extends AbstractBagVisitor implements
        Writer {

    private final ArrayList<ProgressListener> progressListeners =
            new ArrayList<ProgressListener>();

    protected BagFactory bagFactory;

    public AbstractWriter(final BagFactory bagFactory) {
        this.bagFactory = bagFactory;
    }

    @Override
    public void addProgressListener(final ProgressListener progressListener) {
        this.progressListeners.add(progressListener);
    }

    @Override
    public void removeProgressListener(final ProgressListener progressListener) {
        this.progressListeners.remove(progressListener);
    }

    protected void progress(final String activity, final String item,
            final long count, final long total) {
        for (final ProgressListener listener : this.progressListeners) {
            listener.reportProgress(activity, item, count, total);
        }
    }

    protected File getTempFile(final File file) {
        return TempFileHelper.getTempFile(file);
    }

    protected abstract Format getFormat();

    protected void switchTemp(final File file) {
        TempFileHelper.switchTemp(file);

    }
}
