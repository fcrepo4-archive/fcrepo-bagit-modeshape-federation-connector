
package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.ManifestWriter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManifestWriterImpl implements ManifestWriter {

    private static final Logger log = LoggerFactory
            .getLogger(ManifestWriterImpl.class);

    private PrintWriter writer = null;

    private String separator = null;

    public ManifestWriterImpl(final OutputStream out, final String separator) {
        try {
            // UTF-8 is the only supported BagIt encoding at present.
            // Fixes #356.
            this.writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            this.separator = separator;
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(final String file, final String fixityValue) {
        this.writer.println(fixityValue + separator + file);
        log.debug(MessageFormat.format(
                "Wrote to manifest:  Filename is {0}.  Fixity is {1}.", file,
                fixityValue));
    }

    public void write(final String file, final String fixityValue,
            final String _separator) {
        if (_separator != null) {
            this.separator = _separator;
        }

        this.writer.println(fixityValue + separator + file);
        log.debug(MessageFormat.format(
                "Wrote to manifest:  Filename is {0}.  Fixity is {1}.", file,
                fixityValue));
    }

    @Override
    public void close() {
        this.writer.close();
    }
}
