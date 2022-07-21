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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DozerConverterTest extends AbstractDozerTest {

    private DozerConverter<String, Integer> converter;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        converter = new DozerConverter<String, Integer>(String.class,
                Integer.class) {

            public Integer convertTo(String source, Integer destination) {
                return Integer.parseInt(source);
            }

            public String convertFrom(Integer source, String destination) {
                return source.toString();
            }
        };
    }

    @Test
    public void test_parameterNotSet() {
        assertThrows(IllegalStateException.class, () -> converter.getParameter());
    }

    @Test
    public void test_convert_exception() {
        assertThrows(MappingException.class, () ->
                converter.convert(Boolean.TRUE, new BigDecimal(1), Boolean.class,
                        BigDecimal.class));
    }

    @Test
    public void test_gettingParameter() {
        converter.setParameter("A");
        assertEquals("A", converter.getParameter());
    }

    @Test
    public void test_convertFromTo() {
        assertEquals("1", converter.convertFrom(Integer.valueOf(1)));
        assertEquals(Integer.valueOf(2), converter.convertTo("2"));

        assertEquals("1", converter.convertFrom(Integer.valueOf(1), "0"));
        assertEquals(Integer.valueOf(2), converter.convertTo("2", Integer.valueOf(0)));
    }

    @Test
    public void test_FullCycle() {
        assertEquals(1, converter.convert(null, "1", Integer.class,
                String.class));
        assertEquals("1", converter.convert(null, Integer.valueOf(1), String.class,
                Integer.class));
    }

    @Test
    public void testObjectType() {
        assertEquals(1, converter
                .convert(null, "1", Object.class, String.class));
        assertEquals("1", converter.convert(null, Integer.valueOf(1), Object.class,
                Integer.class));
    }

    @Test
    public void testAutoboxing() {
        assertEquals(1, converter.convert(null, "1", int.class, String.class));
    }

    @Test
    public void testPrimitiveToPrimitive() {
        DozerConverter<Integer, Double> converter = new DozerConverter<Integer, Double>(
                Integer.class, Double.class) {

            @Override
            public Double convertTo(Integer source, Double destination) {
                return Double.valueOf(Integer.toString(source));
            }

            @Override
            public Integer convertFrom(Double source, Integer destination) {
                return Integer.valueOf(Double.toString(source));
            }
        };

        converter.convert(1d, 2, double.class, int.class);
    }

    @Test
    public void test_hierarchy() {
        DozerConverter<Number, Integer> converter = new DozerConverter<Number, Integer>(
                Number.class, Integer.class) {

            public Integer convertTo(Number source, Integer destination) {
                return source.intValue();
            }

            public Number convertFrom(Integer source, Number destination) {
                return source;
            }
        };

        assertEquals(Integer.valueOf(1), converter.convert(null, Integer.valueOf(1),
                Number.class, Integer.class));

        assertEquals(Integer.valueOf(1), converter.convert(null, Double.valueOf(1),
                Integer.class, Number.class));
    }

    @Test
    public void testAssignments() {
        DozerConverter<Number, Number> converter = new DozerConverter<Number, Number>(
                Number.class, Number.class) {

            @Override
            public Number convertFrom(Number source, Number destination) {
                return source;
            }

            @Override
            public Number convertTo(Number source, Number destination) {
                return source;
            }
        };
        assertEquals(Integer.valueOf(1), converter.convert(null, Integer.valueOf(1),
                Long.class, Integer.class));
        assertEquals(Integer.valueOf(11), converter.convert(null, Integer.valueOf(11),
                Object.class, Integer.class));
    }

    @Test
    public void testAssignments2() {
        DozerConverter<String, Number> converter = new DozerConverter<String, Number>(
                String.class, Number.class) {

            @Override
            public String convertFrom(Number source, String destination) {
                return source.toString();
            }

            @Override
            public Number convertTo(String source, Number destination) {
                return Long.parseLong(source);
            }

        };

        assertEquals(Long.valueOf(1L), converter.convert(null, new String("1"),
                Long.class, Object.class));
        assertEquals(new String("1"), converter.convert(null, Integer.valueOf(1),
                Object.class, Integer.class));
    }

}
