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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.modeshape.jcr.value.Name;
import org.modeshape.jcr.value.Property;
import org.modeshape.jcr.value.PropertyFactory;
import org.modeshape.jcr.value.ValueFactories;

public class BagItExtraPropertiesStoreTest {

    BagItExtraPropertiesStore store;

    BagItConnector connector;

    @Before
    public void setUp() {
        connector = mock(BagItConnector.class);
        final ValueFactories values = mock(ValueFactories.class);
        final PropertyFactory properties = mock(PropertyFactory.class);
        when(connector.getValueFactories()).thenReturn(values);
        when(connector.getPropertyFactory()).thenReturn(properties);
        store = new BagItExtraPropertiesStore(connector);
    }

    @Test
    public void testRead() {
        final BagInfo mockBI = getMockBagInfo();
        when(connector.getBagInfo("/foo")).thenReturn(mockBI);
        Map<Name, Property> props = store.getProperties("/foo");
        verify(connector).getBagInfo("/foo");
        verify(mockBI).getProperties();
        props = store.getProperties("/non/existent");
        assertEquals(BagItExtraPropertiesStore.EMPTY, props);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateProperties() throws IOException {
        final BagInfo mockBI = getMockBagInfo();
        when(connector.getBagInfo("/foo")).thenReturn(mockBI);
        final Map<Name, Property> mockProps = mock(Map.class);
        store.updateProperties("/foo", mockProps);
        verify(connector).getBagInfo("/foo");
        verify(mockBI).setProperties(any(Map.class));
        verify(mockBI).save();
        verify(mockBI).getProperties();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStoreProperties() throws IOException {
        final BagInfo mockBI = getMockBagInfo();
        when(connector.getBagInfo("/foo")).thenReturn(mockBI);
        final Map<Name, Property> mockProps = mock(Map.class);
        store.storeProperties("/foo", mockProps);
        verify(connector).getBagInfo("/foo");
        verify(mockBI).setProperties(any(Map.class));
        verify(mockBI).save();
    }

    @Test
    public void testRemove() throws IOException {
        final BagInfo mockBI = getMockBagInfo();
        when(mockBI.exists()).thenReturn(true);
        when(connector.getBagInfo("/foo")).thenReturn(mockBI);
        mock(Map.class);
        store.removeProperties("/foo");
        verify(connector).getBagInfo("/foo");
        verify(mockBI).delete();
        verify(mockBI).save();
    }

    private BagInfo getMockBagInfo() {
        final BagInfo mock = mock(BagInfo.class);
        return mock;
    }

}
