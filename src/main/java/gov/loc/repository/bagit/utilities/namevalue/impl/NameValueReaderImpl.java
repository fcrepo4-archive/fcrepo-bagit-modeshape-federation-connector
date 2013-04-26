
package gov.loc.repository.bagit.utilities.namevalue.impl;

import gov.loc.repository.bagit.utilities.namevalue.NameValueReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameValueReaderImpl implements NameValueReader {

    private static final Logger log = LoggerFactory
            .getLogger(NameValueReaderImpl.class);

    private final Deque<String> lines = new ArrayDeque<String>();

    private final String type;

    public NameValueReaderImpl(final String encoding, final InputStream in,
            final String type) {
        this.type = type;

        InputStreamReader fr = null;
        BufferedReader reader = null;
        try {
            // Replaced FileReader with InputStreamReader since all bagit manifest and metadata files must be UTF-8
            // encoded.  If UTF-8 is not explicitly set then data will be read in default native encoding.
            fr = new InputStreamReader(in, encoding);
            reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                lines.addLast(line);
                line = reader.readLine();
            }
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(fr);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public boolean hasNext() {
        if (lines.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public NameValue next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        //Split the first line
        final String line = this.lines.removeFirst();
        final String[] splitString = line.split(" *: *", 2);
        final String name = splitString[0];
        String value = null;
        if (splitString.length == 2) {
            value = splitString[1].trim();
            while (!this.lines.isEmpty() &&
                    this.lines.getFirst().matches("^( |\\t)+.+$")) {
                value +=
                        " " +
                                this.lines.removeFirst().replaceAll(
                                        "^( |\\t)+", "");
            }
        }
        final NameValue ret = new NameValue(name, value);
        log.debug(MessageFormat.format("Read from {0}: {1}", this.type, ret
                .toString()));
        return ret;

    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
