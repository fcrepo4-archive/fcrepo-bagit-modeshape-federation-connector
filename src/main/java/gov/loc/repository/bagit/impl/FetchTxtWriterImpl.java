
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxtWriter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchTxtWriterImpl implements FetchTxtWriter {

    private static final Logger log = LoggerFactory
            .getLogger(FetchTxtWriterImpl.class);

    public static final String SEPARATOR = "  ";

    private PrintWriter writer = null;

    public FetchTxtWriterImpl(final OutputStream out) {
        this.writer = new PrintWriter(out);
    }

    @Override
    public void write(final String filename, final Long size, final String url) {
        String sizeString = FetchTxt.NO_SIZE_MARKER;
        if (size != null) {
            sizeString = size.toString();
        }
        try {
            final String newUrl = url.replaceAll(" ", "%20");
            this.writer.println(newUrl + SEPARATOR + sizeString + SEPARATOR +
                    filename);
            log.debug(MessageFormat
                    .format("Wrote to fetch.txt:  Filename is {0}.  Size is {1}. Url is {2}.",
                            filename, size, newUrl));
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void close() {
        this.writer.close();
    }
}
