
package gov.loc.repository.bagit;

import java.io.Closeable;
import java.text.MessageFormat;
import java.util.Iterator;

public interface ManifestReader extends
        Iterator<ManifestReader.FilenameFixity>, Closeable {

    public class FilenameFixity {

        private String filename;

        private String fixityValue;

        public FilenameFixity(final String file, final String fixityValue) {
            this.filename = file;
            this.fixityValue = fixityValue;
        }

        public FilenameFixity() {
        }

        public void setFilename(final String file) {
            this.filename = file;
        }

        public String getFilename() {
            return filename;
        }

        public void setFixityValue(final String fixityValue) {
            this.fixityValue = fixityValue;
        }

        public String getFixityValue() {
            return fixityValue;
        }

        @Override
        public String toString() {
            return MessageFormat.format("Filename is {0}.  Fixity is {1}.",
                    this.filename, this.fixityValue);
        }
    }
}
