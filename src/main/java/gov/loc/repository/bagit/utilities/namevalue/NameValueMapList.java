
package gov.loc.repository.bagit.utilities.namevalue;

import gov.loc.repository.bagit.utilities.namevalue.NameValueReader.NameValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface NameValueMapList extends Map<String, String>,
        Iterable<NameValue> {

    /*
     * Appends name-values.
     * Any exists name-values with the same key are not touched.
     */
    void putList(final String key, final Collection<String> values);

    /*
     * Appends name-values.
     * Any exists name-values with the same key are not touched.
     */
    void putListAll(final Collection<NameValue> nameValues);

    /*
     * Appends name-value.
     * Any exists name-values with the same key are not touched.
     */
    void putList(final NameValue nameValue);

    /*
     * Appends name-value.
     * Any exists name-values with the same key are not touched.
     */
    void putList(final String key, final String value);

    /*
     * Same semantics as List.remove()
     */
    boolean removeList(final String key, final String value);

    /*
     * Same semantics as List.remove()
     */
    boolean removeList(final NameValue nameValue);

    boolean removeAllList(final String key);

    List<String> getList(final String key);

    /*
     * Same semantics as Map.put()
     */
    String put(final NameValue nameValue);

    List<NameValue> asList();

    int sizeList();
}
