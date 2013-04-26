
package gov.loc.repository.bagit.progresslistener;

import gov.loc.repository.bagit.ProgressListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CompositeProgressListener implements ProgressListener {

    private final List<ProgressListener> listeners = Collections
            .synchronizedList(new ArrayList<ProgressListener>());

    public CompositeProgressListener() {
    }

    public CompositeProgressListener(
            final Collection<ProgressListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public CompositeProgressListener(final ProgressListener[] listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public List<ProgressListener> getProgressListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    public void addProgressListener(final ProgressListener listener) {
        this.listeners.add(listener);
    }

    public void removeProgressListener(final ProgressListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void reportProgress(final String activity, final Object item,
            final Long count, final Long total) {
        for (final ProgressListener listener : this.listeners) {
            listener.reportProgress(activity, item, count, total);
        }
    }

}
