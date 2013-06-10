
package org.fcrepo.federation.bagit;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.Map;

import org.modeshape.jcr.cache.DocumentStoreException;
import org.modeshape.jcr.federation.spi.ExtraPropertiesStore;
import org.modeshape.jcr.value.Name;
import org.modeshape.jcr.value.Property;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableMap;

/**
 * ExtraPropertiesStore implementation which stores properties in bag-info.txt.
 * @see https://tools.ietf.org/html/draft-kunze-bagit-08#section-2.2.2
**/
public class BagItExtraPropertiesStore implements ExtraPropertiesStore {

    private static final Logger logger =
            getLogger(BagItExtraPropertiesStore.class);

    private final BagItConnector connector;

    protected static final Map<Name, Property> EMPTY = emptyMap();

    protected BagItExtraPropertiesStore(final BagItConnector connector) {
        this.connector = connector;
    }

    @Override
    public void storeProperties(final String id,
            final Map<Name, Property> properties) {
        storeProperties(connector.getBagInfo(id), properties);
    }

    private void storeProperties(final BagInfo bagInfo,
            final Map<Name, Property> properties) {
        if (bagInfo == null) {
            return;
        }

        try {
            bagInfo.setProperties(properties);
            bagInfo.save();
        } catch (final Exception ex) {
            throw new DocumentStoreException(
                    "Error in storing properties for " + bagInfo.bagID +
                            " at " + bagInfo.getFilepath(), ex);
        }
    }

    @Override
    public void updateProperties(final String id,
            final Map<Name, Property> properties) {
        final BagInfo bagInfo = connector.getBagInfo(id);
        if (bagInfo == null) {
            return;
        }
        final Map<Name, Property> existing = bagInfo.getProperties();
        for (final Map.Entry<Name, Property> entry : properties.entrySet()) {
            final Name name = entry.getKey();
            final Property prop = entry.getValue();
            if (prop == null) {
                existing.remove(name);
            } else {
                existing.put(name, prop);
            }
        }
        storeProperties(bagInfo, existing);
    }

    @Override
    public Map<Name, Property> getProperties(final String id) {

        final BagInfo bagInfo = connector.getBagInfo(id);
        if (bagInfo == null) {
        	if(!"/".equals(id)) logger.trace("No bag-info.txt for " + id);
            return EMPTY;
        }
        logger.trace("Operating on bagInfoFile(" + id + "):" +
                bagInfo.getFilepath());
        try {
            return bagInfo.getProperties();
        } catch (final Exception ex) {
            throw new DocumentStoreException(id, ex);
        }
    }

    @Override
    public boolean removeProperties(final String id) {
        final BagInfo bagInfo = connector.getBagInfo(id);
        if (!bagInfo.exists()) {
            return false;
        } else {
            try {
                final boolean result = bagInfo.delete();
                bagInfo.save();
                return result;
            } catch (final IOException ex) {
                throw new DocumentStoreException(
                        "Error in removing properties for " + bagInfo.bagID +
                                " at " + bagInfo.getFilepath(), ex);
            }
        }
    }

    private static final Map<Name, Property> emptyMap() {
        final ImmutableMap.Builder<Name, Property> properties =
                ImmutableMap.builder();
        return properties.build();
    }

}
