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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.vo.InsideTestObject;
import com.github.dozermapper.core.vo.SimpleObj;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionUtilsTest extends AbstractDozerTest {

    @Test
    public void testIsList() {
        Object[] values = new Object[]{new ArrayList(), new Vector(), new LinkedList()};
        for (int i = 0; i < values.length; i++) {
            assertTrue(CollectionUtils.isList(values[i].getClass()));
        }
    }

    @Test
    public void testIsSet() {
        Object[] values = new Object[]{new TreeSet(), new HashSet(), new HashSet()};
        for (int i = 0; i < values.length; i++) {
            assertTrue(CollectionUtils.isSet(values[i].getClass()));
        }
    }

    @Test
    public void testIsArray() {
        Object[] values = new Object[]{new int[3], new InsideTestObject[2], new String[3]};
        for (int i = 0; i < values.length; i++) {
            assertTrue(CollectionUtils.isArray(values[i].getClass()));
        }
    }

    @Test
    public void testIsPrimitiveArray() {
        Object[] values = new Object[]{new int[3], new long[2], new boolean[3]};
        for (int i = 0; i < values.length; i++) {
            assertTrue(CollectionUtils.isPrimitiveArray(values[i].getClass()));
        }
    }

    @Test
    public void testIsPrimitiveArray_False() {
        Object[] values = new Object[]{new String[3], new Date[2], new SimpleObj[3]};
        for (int i = 0; i < values.length; i++) {
            assertFalse(CollectionUtils.isPrimitiveArray(values[i].getClass()));
        }
    }

    @Test
    public void testGetValueFromCollection() {
        String sysTime = String.valueOf(System.currentTimeMillis());
        String[] input = new String[]{"zer", "one", "two", "three", "four", sysTime, "six", "seven"};
        Object result = CollectionUtils.getValueFromCollection(input, 5);

        assertEquals(sysTime, result, "invalid result");
    }

    @Test
    public void testLengthOfCollection() {
        String[] input = new String[]{"zer", "one", "two", "three", "four"};
        assertEquals(input.length, CollectionUtils.getLengthOfCollection(input), "invalid array size");
    }

    @Test
    public void testCreateNewSet_Default() {
        Set<?> result = CollectionUtils.createNewSet(Set.class);
        assertNotNull(result, "new set should not be null");
    }

    @Test
    public void testCreateNewSet_SortedSetDefault() {
        Set<?> result = CollectionUtils.createNewSet(SortedSet.class);
        assertNotNull(result, "new set should not be null");
        assertTrue(result instanceof SortedSet, "new set should be instance of SortedSet");
    }

    @Test
    public void testCreateNewSet_FromExistingSet() {
        Set<String> src = new HashSet<>();
        src.add("test1");
        src.add("test2");
        Set<?> result = CollectionUtils.createNewSet(Set.class, src);
        assertNotNull(result, "new set should not be null");
        assertEquals(src, result, "new set should equal src set");
    }

    @Test
    public void testConvertPrimitiveArrayToList() {
        int[] srcArray = new int[]{5, 10, 15};
        List<?> result = CollectionUtils.convertPrimitiveArrayToList(srcArray);
        assertEquals(srcArray.length, result.size(), "invalid result size");

        for (int i = 0; i < srcArray.length; i++) {
            Integer srcValue = Integer.valueOf(srcArray[i]);
            Integer resultValue = (Integer) result.get(i);
            assertEquals(srcValue, resultValue, "invalid result entry value");
        }
    }

    @Test
    public void testConvertListToArray() {
        List<String> src = Arrays.asList("a", "b");
        String[] result = (String[]) CollectionUtils.convertListToArray(src, String.class);
        assertTrue(Arrays.equals(new String[]{"a", "b"}, result), "wrong result value");
    }

    @Test
    public void testCreateNewSet_ExistingValue() {
        Collection<String> src = new HashSet<>();
        src.add("a");
        src.add("b");
        Set<?> result = CollectionUtils.createNewSet(TreeSet.class, src);
        assertEquals(src, result, "wrong result value");
    }

    @Test
    public void testCreateNewSet() {
        Set<?> result = CollectionUtils.createNewSet(HashSet.class);
        assertNotNull(result, "should be not null");
        assertEquals(0, result.size(), "shoulb be size zero");
    }

}
