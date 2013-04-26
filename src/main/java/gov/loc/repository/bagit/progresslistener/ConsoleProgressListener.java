
package gov.loc.repository.bagit.progresslistener;

import gov.loc.repository.bagit.ProgressListener;

import java.io.Console;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsoleProgressListener extends Object implements ProgressListener {

    private final Console console = System.console();

    private long nextUpdate = System.currentTimeMillis();

    private int lastLineLength = 0;

    private final AtomicBoolean updating = new AtomicBoolean(false);

    @Override
    public void reportProgress(final String activity, final Object item,
            final Long count, final Long total) {
        if (console != null) {
            final long now = System.currentTimeMillis();
            final long next = this.nextUpdate;

            if (now >= next) {
                final String msg =
                        ProgressListenerHelper.format(activity, item, count,
                                total);

                // We use an atomic boolean here so that we don't have to lock
                // every single time.  This keeps contention down on this
                // bottleneck.
                if (this.updating.compareAndSet(false, true)) {
                    try {
                        final int lastLength = this.lastLineLength;

                        this.backup(lastLength);
                        this.console.format(msg);

                        if (msg.length() < lastLength) {
                            final int spacesNeeded = lastLength - msg.length();
                            this.spaces(spacesNeeded);
                            this.backup(spacesNeeded);
                        }

                        this.console.flush();

                        this.lastLineLength = msg.length();
                        this.nextUpdate = now + 1000;
                    } finally {
                        this.updating.set(false);
                    }
                }
            }
        }
    }

    private void backup(final int length) {
        for (int i = 0; i < length; i++) {
            this.console.format("\b");
        }
    }

    private void spaces(final int length) {
        for (int i = 0; i < length; i++) {
            this.console.format(" ");
        }
    }
}
