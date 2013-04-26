
package gov.loc.repository.bagit.utilities;

import gov.loc.repository.bagit.Cancellable;
import gov.loc.repository.bagit.ProgressListenable;
import gov.loc.repository.bagit.ProgressListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LongRunningOperationBase implements Cancellable,
        ProgressListenable {

    private boolean isCancelled = false;

    private final ArrayList<ProgressListener> progressListeners =
            new ArrayList<ProgressListener>();

    private final Set<ProgressListenable> chainedProgressListenables =
            new HashSet<ProgressListenable>();

    private final Set<Cancellable> chainedCancellables =
            new HashSet<Cancellable>();

    @Override
    public void addProgressListener(final ProgressListener progressListener) {
        this.progressListeners.add(progressListener);
        for (final ProgressListenable progressListenable : this.chainedProgressListenables) {
            progressListenable.addProgressListener(progressListener);
        }
    }

    @Override
    public void removeProgressListener(final ProgressListener progressListener) {
        this.progressListeners.remove(progressListener);
        for (final ProgressListenable progressListenable : this.chainedProgressListenables) {
            progressListenable.removeProgressListener(progressListener);
        }
    }

    @Override
    public void cancel() {
        this.isCancelled = true;
        for (final Cancellable cancellable : this.chainedCancellables) {
            cancellable.cancel();
        }
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    protected void addChainedProgressListenable(
            final ProgressListenable progressListenable) {
        this.chainedProgressListenables.add(progressListenable);
    }

    protected void removeChainedProgressListenable(
            final ProgressListenable progressListenable) {
        this.chainedProgressListenables.remove(progressListenable);
    }

    protected void addChainedCancellable(final Cancellable cancellable) {
        this.chainedCancellables.add(cancellable);
    }

    protected void removeChainedCancellable(final Cancellable cancellable) {
        this.chainedCancellables.remove(cancellable);
    }

    protected void progress(final String activity, final Object item) {
        this.progress(activity, item, (Long) null, (Long) null);
    }

    protected void progress(final String activity, final Object item,
            final Long count, final Long total) {
        for (final ProgressListener listener : this.progressListeners) {
            listener.reportProgress(activity, item, count, total);
        }
    }

    protected void progress(final String activity, final Object item,
            final Integer count, final Integer total) {
        this.progress(activity, item, count == null ? (Long) null : new Long(
                count), total == null ? (Long) null : new Long(total));
    }

    protected void delegateProgress(final ProgressListenable listenable) {
        listenable.addProgressListener(new ProgressDelegate());
    }

    private class ProgressDelegate implements ProgressListener {

        @Override
        public void reportProgress(final String activity, final Object item,
                final Long count, final Long total) {
            LongRunningOperationBase.this
                    .progress(activity, item, count, total);
        }
    }
}
