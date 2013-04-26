
package gov.loc.repository.bagit.progresslistener;

import gov.loc.repository.bagit.ProgressListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingProgressListener implements ProgressListener {

    private final Logger log;

    public LoggingProgressListener(final String logName) {
        this.log = LoggerFactory.getLogger(logName);
    }

    public LoggingProgressListener(final Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    public LoggingProgressListener() {
        this(LoggingProgressListener.class);
    }

    @Override
    public void reportProgress(final String activity, final Object item,
            final Long count, final Long total) {
        log.info(ProgressListenerHelper.format(activity, item, count, total));
    }
}
