/*
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceLoaderTest extends AbstractDozerTest {

    private ResourceLoader loader = new ResourceLoader(getClass().getClassLoader());

    @Test
    public void testResourceNotFound() {
        assertNull(loader.getResource(String.valueOf(System.currentTimeMillis())), "file URL should not have been found");
    }

    @Test
    public void testGetResourceWithWhitespace() {
        URL url = loader.getResource("mappings/contextMapping.xml ");
        assertNotNull(url, "URL should not be null");
    }

    @Test
    public void testGetResource_FileOutsideOfClasspath() throws Exception {
        // Create temp file.
        File temp = File.createTempFile("dozerfiletest", ".txt");

        // Delete temp file when program exits.
        temp.deleteOnExit();

        String resourceName = "file:" + temp.getAbsolutePath();
        URL url = loader.getResource(resourceName);
        assertNotNull(url, "URL should not be null");

        InputStream is = url.openStream();
        assertNotNull(is, "input stream should not be null");
    }

    @Test
    public void testGetResouce_MalformedUrl() {
        assertThrows(MappingException.class, () -> loader.getResource("foo:bar"));
    }

    @Test
    public void testGetResource_FileOutsideOfClasspath_NotFound() throws Exception {
        assertThrows(IOException.class, () -> {
            URL url = loader.getResource("file:" + System.currentTimeMillis());
            assertNotNull(url, "URL should not be null");
            url.openStream();
        });
    }

    @Test
    public void testGetResource_FileOutsideOfClasspath_InvalidFormat() {
        // when using a file outside of classpath the file name must be prepended with "file:"
        URL url = loader.getResource(String.valueOf(System.currentTimeMillis()));
        assertNull(url, "URL should be null");
    }
}
