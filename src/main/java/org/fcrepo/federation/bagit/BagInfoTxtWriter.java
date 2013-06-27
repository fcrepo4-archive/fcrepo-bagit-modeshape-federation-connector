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

import gov.loc.repository.bagit.impl.BagInfoTxtWriterImpl;

import java.io.Closeable;
import java.io.OutputStream;

/**
 * Just a proxy to implement Closeable
 * 
 * @author ba2213
 */
public class BagInfoTxtWriter extends BagInfoTxtWriterImpl implements Closeable {

    public BagInfoTxtWriter(final OutputStream out, final String encoding) {
        super(out, encoding);
    }

    public BagInfoTxtWriter(final OutputStream out, final String encoding,
            final int lineLength, final int indent) {
        super(out, encoding, lineLength, indent);
    }

}
