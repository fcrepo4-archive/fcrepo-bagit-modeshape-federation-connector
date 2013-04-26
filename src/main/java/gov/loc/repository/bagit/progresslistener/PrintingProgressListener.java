
package gov.loc.repository.bagit.progresslistener;

import gov.loc.repository.bagit.ProgressListener;

public class PrintingProgressListener implements ProgressListener {

    @Override
    public void reportProgress(final String activity, final Object item,
            final Long count, final Long total) {
        System.out.println(ProgressListenerHelper.format(activity, item, count,
                total));
    }
}
