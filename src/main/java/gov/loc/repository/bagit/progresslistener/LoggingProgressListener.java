package gov.loc.repository.bagit.progresslistener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.ProgressListener;

public class LoggingProgressListener implements ProgressListener 
{
    private Logger log;
    
    public LoggingProgressListener(String logName)
    {
    	this.log = LoggerFactory.getLogger(logName);
    }
    
    public LoggingProgressListener(Class<?> clazz)
    {
    	this.log = LoggerFactory.getLogger(clazz);
    }
    
    public LoggingProgressListener()
    {
    	this(LoggingProgressListener.class);
    }

	@Override
	public void reportProgress(String activity, Object item, Long count, Long total)
	{
		log.info(ProgressListenerHelper.format(activity, item, count, total));
	}
}
