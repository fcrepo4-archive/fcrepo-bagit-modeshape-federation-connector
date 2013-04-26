
package gov.loc.repository.bagit.utilities.namevalue;

import java.io.Closeable;

public interface NameValueWriter extends Closeable {

    void write(final String name, final String value);
}
