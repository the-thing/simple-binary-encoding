/*
 * Copyright 2013-2025 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.codec.java;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.PrimitiveValue;
import uk.co.real_logic.sbe.ir.Encoding;
import uk.co.real_logic.sbe.otf.Types;

import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypesTest
{
    private static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();
    private static final int BUFFER_CAPACITY = 64;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[BUFFER_CAPACITY]);

    @Test
    void shouldTestBitInByte()
    {
        final byte bits = (byte)0b1000_0000;
        final int bufferIndex = 8;
        final int bitIndex = 7;
        buffer.putByte(bufferIndex, bits);

        for (int i = 0; i < 8; i++)
        {
            final boolean result = 0 != (buffer.getByte(bufferIndex) & (1 << i));
            if (bitIndex == i)
            {
                assertTrue(result);
            }
            else
            {
                assertFalse(result, "bit set i = " + i);
            }
        }
    }

    @Test
    void shouldSetBitInByte()
    {
        final int bufferIndex = 8;

        int total = 0;
        for (int i = 0; i < 8; i++)
        {
            byte bits = buffer.getByte(bufferIndex);
            bits = (byte)(bits | (1 << i));
            buffer.putByte(bufferIndex, bits);
            total += (1 << i);
            assertEquals((byte)total, buffer.getByte(bufferIndex));
        }
    }

    @Test
    void shouldTestBitInShort()
    {
        final short bits = (short)0b0000_0000_0000_0100;
        final int bufferIndex = 8;
        final int bitIndex = 2;
        buffer.putShort(bufferIndex, bits, BYTE_ORDER);

        for (int i = 0; i < 16; i++)
        {
            final boolean result = 0 != (buffer.getShort(bufferIndex, BYTE_ORDER) & (1 << i));
            if (bitIndex == i)
            {
                assertTrue(result);
            }
            else
            {
                assertFalse(result, "bit set i = " + i);
            }
        }
    }

    @Test
    void shouldSetBitInShort()
    {
        final int bufferIndex = 8;

        int total = 0;
        for (int i = 0; i < 16; i++)
        {
            short bits = buffer.getShort(bufferIndex, BYTE_ORDER);
            bits = (short)(bits | (1 << i));
            buffer.putShort(bufferIndex, bits, BYTE_ORDER);
            total += (1 << i);
            assertEquals((short)total, buffer.getShort(bufferIndex, BYTE_ORDER));
        }
    }

    @Test
    void shouldTestBitInInt()
    {
        final int bits = 0b0000_0000_0000_0000_0000_0000_0000_0100;
        final int bufferIndex = 8;
        final int bitIndex = 2;
        buffer.putInt(bufferIndex, bits, BYTE_ORDER);

        for (int i = 0; i < 32; i++)
        {
            final boolean result = 0 != (buffer.getInt(bufferIndex, BYTE_ORDER) & (1 << i));
            if (bitIndex == i)
            {
                assertTrue(result);
            }
            else
            {
                assertFalse(result, "bit set i = " + i);
            }
        }
    }

    @Test
    void shouldSetBitInInt()
    {
        final int bufferIndex = 8;
        long total = 0;

        for (int i = 0; i < 32; i++)
        {
            int bits = buffer.getInt(bufferIndex, BYTE_ORDER);
            bits = bits | (1 << i);
            buffer.putInt(bufferIndex, bits, BYTE_ORDER);
            total += (1 << i);
            assertEquals((int)total, buffer.getInt(bufferIndex, BYTE_ORDER));
        }
    }

    @Test
    void shouldNotExpandJsonDirectBufferFloatToDouble()
    {
        final StringBuilder sb = new StringBuilder();
        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[4]);

        Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        buffer.putFloat(0, PrimitiveValue.MIN_VALUE_FLOAT);
        Types.appendAsJsonString(sb, buffer, 0, encoding);
        assertEquals("-3.4028235E38", sb.toString());

        sb.setLength(0);
        encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        buffer.putFloat(0, PrimitiveValue.MAX_VALUE_FLOAT);
        Types.appendAsJsonString(sb, buffer, 0, encoding);
        assertEquals("3.4028235E38", sb.toString());

        sb.setLength(0);
        encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        buffer.putFloat(0, Float.MIN_NORMAL);
        Types.appendAsJsonString(sb, buffer, 0, encoding);
        assertEquals("1.1754944E-38", sb.toString());

        sb.setLength(0);
        encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        buffer.putFloat(0, 0.1f);
        Types.appendAsJsonString(sb, buffer, 0, encoding);
        assertEquals("0.1", sb.toString());
    }

    @Test
    void shouldNotExpandJsonPrimitiveValueFloatToDouble()
    {
        final StringBuilder sb = new StringBuilder();

        Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        Types.appendAsJsonString(sb, new PrimitiveValue(PrimitiveValue.MIN_VALUE_FLOAT, 4), encoding);
        assertEquals("-3.4028235E38", sb.toString());

        sb.setLength(0);
        encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        Types.appendAsJsonString(sb, new PrimitiveValue(PrimitiveValue.MAX_VALUE_FLOAT, 4), encoding);
        assertEquals("3.4028235E38", sb.toString());

        sb.setLength(0);
        encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        Types.appendAsJsonString(sb, new PrimitiveValue(Float.MIN_NORMAL, 4), encoding);
        assertEquals("1.1754944E-38", sb.toString());

        sb.setLength(0);
        encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        Types.appendAsJsonString(sb, new PrimitiveValue(0.1f, 4), encoding);
        assertEquals("0.1", sb.toString());
    }
}
