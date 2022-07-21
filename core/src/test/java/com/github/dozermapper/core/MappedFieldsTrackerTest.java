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
package com.github.dozermapper.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MappedFieldsTrackerTest extends AbstractDozerTest {


    private MappedFieldsTracker tracker;

    @BeforeEach
    public void setUp() {
        tracker = new MappedFieldsTracker();
    }

    @Test
    public void testPut_OK() {
        tracker.put("", "1");
        assertEquals("1", tracker.getMappedValue("", String.class));
    }

    @Test
    public void testPut_Interface() {
        tracker.put("", "1");
        tracker.put("", new HashSet());
        assertEquals(new HashSet(), tracker.getMappedValue("", Set.class));
    }

    @Test
    public void testGetMappedValue() {
        assertNull(tracker.getMappedValue("", String.class));
    }

    @Test
    public void testGetMappedValue_NoSuchType() {
        tracker.put("", new HashSet());
        assertNull(tracker.getMappedValue("", String.class));
    }

    @Test
    public void doesNotCallEqualsOrHashCode() {
        Boom src = new Boom();
        Boom dest = new Boom();

        tracker.put(src, dest);

        Object result = tracker.getMappedValue(src, Boom.class);
        assertSame(dest, result);
    }

    @Test
    public void testGetMappedValue_honorsMapId() {
        tracker.put("", "42", "someId");
        assertNull(tracker.getMappedValue("", String.class));
        assertEquals("42", tracker.getMappedValue("", String.class, "someId"));
        assertNull(tracker.getMappedValue("", String.class, "brandNewMapId"));
    }

    public static class Boom {
        @Override
        public int hashCode() {
            throw new RuntimeException();
        }

        @Override
        public boolean equals(Object obj) {
            throw new RuntimeException();
        }
    }

    @Test
    public void testTransaction_commit() {
        tracker.put("1", "1");

        assertFalse(tracker.hasTransaction());
        Integer txId = tracker.startTransaction();
        assertTrue(tracker.hasTransaction());
        assertEquals(Integer.valueOf(0), txId);

        tracker.put("2", "2");
        assertEquals("1", tracker.getMappedValue("1", String.class));
        assertEquals("2", tracker.getMappedValue("2", String.class));

        tracker.commitTransaction(txId);
        assertFalse(tracker.hasTransaction());
        assertEquals("1", tracker.getMappedValue("1", String.class));
        assertEquals("2", tracker.getMappedValue("2", String.class));
    }

    @Test
    public void testTransaction_rollback() {
        tracker.put("1", "1");

        assertFalse(tracker.hasTransaction());
        Integer txId = tracker.startTransaction();
        assertTrue(tracker.hasTransaction());
        assertEquals(Integer.valueOf(0), txId);

        tracker.put("2", "2");
        assertEquals("1", tracker.getMappedValue("1", String.class));
        assertEquals("2", tracker.getMappedValue("2", String.class));

        tracker.rollbackTransaction(txId);
        assertFalse(tracker.hasTransaction());
        assertEquals("1", tracker.getMappedValue("1", String.class));
        assertNull(tracker.getMappedValue("2", String.class));
    }

    @Test
    public void testTransaction_nestedRollback() {
        tracker.put("1", "1");

        // start root transaction
        Integer txIdRoot = tracker.startTransaction();
        assertTrue(tracker.hasTransaction());
        tracker.put("2", "2");

        // start nested transaction
        Integer txIdNested = tracker.startTransaction();
        assertTrue(tracker.hasTransaction());
        tracker.put("3", "3");

        // rollback nested transaction
        tracker.rollbackTransaction(txIdNested);
        assertTrue(tracker.hasTransaction());
        assertNull(tracker.getMappedValue("3", String.class));
        assertEquals("2", tracker.getMappedValue("2", String.class));
        assertEquals("1", tracker.getMappedValue("1", String.class));

        // commit root transaction
        tracker.commitTransaction(txIdRoot);
        assertFalse(tracker.hasTransaction());
        assertEquals("2", tracker.getMappedValue("2", String.class));
        assertEquals("1", tracker.getMappedValue("1", String.class));
    }

    @Test
    public void testTransaction_nestedCommit() {
        tracker.put("1", "1");

        // start root transaction
        Integer txIdRoot = tracker.startTransaction();
        assertTrue(tracker.hasTransaction());
        tracker.put("2", "2");

        // start nested transaction
        Integer txIdNested = tracker.startTransaction();
        assertTrue(tracker.hasTransaction());
        tracker.put("3", "3");

        // rollback nested transaction
        tracker.commitTransaction(txIdNested);
        assertTrue(tracker.hasTransaction());
        assertEquals("3", tracker.getMappedValue("3", String.class));
        assertEquals("2", tracker.getMappedValue("2", String.class));
        assertEquals("1", tracker.getMappedValue("1", String.class));

        // commit root transaction
        tracker.commitTransaction(txIdRoot);
        assertFalse(tracker.hasTransaction());
        assertEquals("3", tracker.getMappedValue("3", String.class));
        assertEquals("2", tracker.getMappedValue("2", String.class));
        assertEquals("1", tracker.getMappedValue("1", String.class));
    }

    @Test
    public void testTransaction_nestedOuterRollback() {
        tracker.put("1", "1");

        // start root transaction
        Integer txIdRoot = tracker.startTransaction();
        assertTrue(tracker.hasTransaction());
        tracker.put("2", "2");

        // start nested transaction
        Integer txIdNested = tracker.startTransaction();
        assertTrue(tracker.hasTransaction());
        tracker.put("3", "3");

        // rollback nested transaction
        tracker.commitTransaction(txIdNested);
        assertTrue(tracker.hasTransaction());
        assertEquals("3", tracker.getMappedValue("3", String.class));
        assertEquals("2", tracker.getMappedValue("2", String.class));
        assertEquals("1", tracker.getMappedValue("1", String.class));

        // commit root transaction
        tracker.rollbackTransaction(txIdRoot);
        assertFalse(tracker.hasTransaction());
        assertNull(tracker.getMappedValue("3", String.class));
        assertNull(tracker.getMappedValue("2", String.class));
        assertEquals("1", tracker.getMappedValue("1", String.class));
    }

    @Test
    public void testTransaction_unknownCommit() {
        Exception e = assertThrows(IllegalStateException.class, () -> tracker.commitTransaction(0));
        assertEquals("No transaction with ID 0", e.getMessage());
    }

    @Test
    public void testTransaction_unknownRollback() {
        Exception e = assertThrows(IllegalStateException.class, () -> tracker.rollbackTransaction(0));
        assertEquals("No transaction with ID 0", e.getMessage());
    }

}
