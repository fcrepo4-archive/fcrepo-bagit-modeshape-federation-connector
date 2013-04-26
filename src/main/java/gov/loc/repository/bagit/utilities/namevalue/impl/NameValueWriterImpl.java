
package gov.loc.repository.bagit.utilities.namevalue.impl;

import gov.loc.repository.bagit.utilities.namevalue.NameValueWriter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameValueWriterImpl implements NameValueWriter {

    private static final Logger log = LoggerFactory
            .getLogger(NameValueWriterImpl.class);

    private PrintWriter writer = null;

    private int lineLength = 79;

    //Default to 4
    private String indent = "   ";

    private String type;

    public NameValueWriterImpl(final OutputStream out, final String encoding,
            final int lineLength, final int indentSpaces, final String type) {
        this.init(out, encoding, type);
        this.lineLength = lineLength;
        this.indent = "";
        for (int i = 0; i < indentSpaces; i++) {
            this.indent += " ";
        }
    }

    public NameValueWriterImpl(final OutputStream out, final String encoding,
            final String type) {
        this.init(out, encoding, type);
    }

    private void init(final OutputStream out, final String encoding,
            final String type) {
        try {
            this.writer =
                    new PrintWriter(new OutputStreamWriter(out, encoding), true);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        this.type = type;
    }

    @Override
    public void write(final String name, final String value) {
        String line = name + ": " + (value != null ? value : "");
        boolean isFirst = true;
        while (line.length() > 0) {
            int workingLength = lineLength;
            if (!isFirst) {
                workingLength = lineLength - this.indent.length();
            }
            String linePart = "";
            if (line.length() <= workingLength) {
                linePart = line;
                line = "";
            } else {
                //Start at lineLength and work backwards until find a space
                int index = workingLength;
                while (index >= 0 && line.charAt(index) != ' ') {
                    index = index - 1;
                }
                if (index < 0) {
                    //Use whole line
                    linePart = line;
                    line = "";
                } else {
                    linePart = line.substring(0, index);
                    line = line.substring(index + 1);
                }

            }
            if (isFirst) {
                isFirst = false;
            } else {
                linePart = this.indent + linePart;
            }
            this.writer.println(linePart);
        }
        log.debug(MessageFormat.format(
                "Wrote to {0}: Name is {1}. Value is {2}.", this.type, name,
                value));
    }

    @Override
    public void close() {
        this.writer.close();
    }
}
