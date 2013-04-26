
package gov.loc.repository.bagit;

import java.io.Closeable;

public interface FetchTxtWriter extends Closeable {

    void write(final String filename, final Long size, final String url);
}
