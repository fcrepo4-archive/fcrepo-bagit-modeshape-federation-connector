
package gov.loc.repository.bagit.impl;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;
import gov.loc.repository.bagit.FetchTxtReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchTxtReaderImpl implements FetchTxtReader {

    private static final Logger log = LoggerFactory
            .getLogger(FetchTxtReaderImpl.class);

    private BufferedReader reader = null;

    private FilenameSizeUrl next = null;

    public FetchTxtReaderImpl(final InputStream in, final String encoding) {
        try {
            final InputStreamReader fr = new InputStreamReader(in, encoding);
            this.reader = new BufferedReader(fr);
            this.setNext();
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        try {
            if (this.reader != null) {
                this.reader.close();
            }
        } catch (final IOException ex) {
            log.error(ex.getMessage(), ex);
        }

    }

    @Override
    public boolean hasNext() {
        if (this.next == null) {
            return false;
        }
        return true;
    }

    private void setNext() {
        try {
            while (true) {
                String line = this.reader.readLine();
                if (line == null) {
                    this.next = null;
                    return;
                } else {
                    line = line.trim();
                }

                if (line.length() > 0) {
                    final String[] splitString = line.split("\\s+", 3);

                    if (splitString.length == 3) {
                        final Long size = null;
                        if (!FetchTxt.NO_SIZE_MARKER.equals(splitString[1])) {
                            Long.parseLong(splitString[1]);
                        }
                        this.next =
                                new FilenameSizeUrl(splitString[2], size,
                                        splitString[0]);
                        return;
                    } else {
                        log.warn(format("Invalid fetch line: {0}", line));
                    }
                }
            }
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public FilenameSizeUrl next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        final FilenameSizeUrl returnFilenameSizeUrl = this.next;
        this.setNext();
        log.debug("Read from fetch.txt: " + returnFilenameSizeUrl.toString());
        return returnFilenameSizeUrl;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();

    }

}
