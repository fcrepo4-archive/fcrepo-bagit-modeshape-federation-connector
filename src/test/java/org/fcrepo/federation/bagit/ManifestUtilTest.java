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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ManifestUtilTest {

    @Test
    public void testIsManifest() throws IOException {
        final File input = mock(File.class);
        when(input.isFile()).thenReturn(true);
        when(input.canRead()).thenReturn(true);

        String fname = "not-a-manifest.txt";
        when(input.getName()).thenReturn(fname);
        assertFalse("\"" + fname + "\" should not be a valid manifest file",
                ManifestUtil.isManifest(input));
        fname = "manifest-md5.txt";
        when(input.getName()).thenReturn(fname);
        assertTrue("\"" + fname + "\" should be a valid manifest file",
                ManifestUtil.isManifest(input));
        fname = "manifest-foobar.txt";
        when(input.getName()).thenReturn(fname);
        assertFalse(
                "Unexpected checksum algorithm \"foobar\" returned valid manifest",
                ManifestUtil.isManifest(input));
    }

    @Test
    public void testIsTagManifest() throws IOException {
        final File input = mock(File.class);
        when(input.isFile()).thenReturn(true);
        when(input.canRead()).thenReturn(true);

        String fname = "not-a-manifest.txt";
        when(input.getName()).thenReturn(fname);
        assertFalse("\"" + fname + "\" should not be a valid manifest file",
                ManifestUtil.isTagManifest(input));
        fname = "tagmanifest-md5.txt";
        when(input.getName()).thenReturn(fname);
        assertTrue("\"" + fname + "\" should be a valid manifest file",
                ManifestUtil.isTagManifest(input));
        fname = "tagmanifest-foobar.txt";
        when(input.getName()).thenReturn(fname);
        assertFalse(
                "Unexpected checksum algorithm \"foobar\" returned valid manifest",
                ManifestUtil.isTagManifest(input));
    }
}
