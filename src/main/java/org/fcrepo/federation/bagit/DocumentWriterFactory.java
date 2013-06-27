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

import org.modeshape.jcr.cache.document.DocumentTranslator;
import org.modeshape.jcr.federation.FederatedDocumentWriter;
import org.modeshape.jcr.federation.spi.DocumentWriter;

/**
 * This class really exists only to facilitate testing around some cyclical
 * dependencies in MODE
 * 
 * @author ba2213
 */
public class DocumentWriterFactory {

    private DocumentTranslator m_translator;

    public DocumentWriterFactory() {

    }

    public DocumentWriterFactory(final DocumentTranslator translator) {
        setTranslator(translator);
    }

    public void setTranslator(final DocumentTranslator translator) {
        m_translator = translator;
    }

    public DocumentWriter getDocumentWriter(final String id) {
        return new FederatedDocumentWriter(m_translator).setId(id);
    }
}
