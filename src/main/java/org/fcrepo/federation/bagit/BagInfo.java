/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.federation.bagit;

import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxtWriter;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;
import gov.loc.repository.bagit.utilities.namevalue.NameValueReader.NameValue;

import java.io.IOException;
import java.util.Map;

import org.fcrepo.federation.bagit.functions.GetBagInfoTxtWriter;
import org.modeshape.jcr.value.Name;
import org.modeshape.jcr.value.NameFactory;
import org.modeshape.jcr.value.Property;
import org.modeshape.jcr.value.PropertyFactory;

import com.google.common.collect.ImmutableMap;

public class BagInfo extends BagInfoTxtImpl {

    /**
     * The ID under which this bag is stored.
     */
    public String bagID;

    private final PropertyFactory m_propertyFactory;

    private final NameFactory m_nameFactory;

    static GetBagInfoTxtWriter getBagInfoTxtWriter = new GetBagInfoTxtWriter();

    public BagInfo(final String bagID, final BagFile bagFile,
            final PropertyFactory propertyFactory,
            final NameFactory nameFactory, final BagConstants bagConstants) {
        super(bagFile, bagConstants);
        this.bagID = bagID;
        m_propertyFactory = propertyFactory;
        m_nameFactory = nameFactory;
    }

    /**
     * Stores this bag-info.txt into its bag.
     */
    public void save() throws IOException {
        ;
        try (BagInfoTxtWriter writer =
                getBagInfoTxtWriter.apply(this.getFilepath())) {
            final Map<Name, Property> properties = getProperties();
            for (final Property jcrProp : properties.values()) {
                final NameValue prop = toBagitProperty(jcrProp);
                writer.write(prop.getName(), prop.getValue());
            }
        }
    }

    public boolean delete() throws IOException {
        final int len = getProperties().size();
        for (final String key : this.keySet()) {
            this.removeAllList(key);
        }
        setProperties(BagItExtraPropertiesStore.EMPTY);
        return len > 0;
    }

    private Name toPropertyName(final NameValue bagitProperty) {
        return m_nameFactory.create("info:fedora/bagit/", bagitProperty
                .getName().replace('-', '.'));
    }

    private Property toJcrProperty(final NameValue bagitProperty) {
        return m_propertyFactory.create(toPropertyName(bagitProperty),
                bagitProperty.getValue());
    }

    private NameValue toBagitProperty(final Property jcrProperty) {
        final NameValue prop = new NameValue();
        prop.setName(jcrProperty.getName().getLocalName().replace('.', '-'));
        prop.setValue(jcrProperty.getString());
        return prop;
    }

    public Map<Name, Property> getProperties() {
        final ImmutableMap.Builder<Name, Property> properties =
                ImmutableMap.builder();
        for (final NameValue key : this.asList()) {
            final Property prop = toJcrProperty(key);
            properties.put(prop.getName(), prop);
        }
        return properties.build();
    }

    public void setProperties(final Map<Name, Property> properties) {
        for (final Property entry : properties.values()) {
            final NameValue bagitProperty = toBagitProperty(entry);
            this.removeAllList(bagitProperty.getName());
            this.putList(bagitProperty);
        }
    }

}
