package uk.co.real_logic.sbe.generation.java;

import extension.foo.MessageHeaderDecoder;
import extension.foo.MessageHeaderEncoder;
import extension.foo.MessageWithInvalidGroupEncodingDecoder;
import extension.foo.MessageWithInvalidGroupEncodingEncoder;
import org.agrona.ExpandableArrayBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FooTest
{

    @Test
    public void foo()
    {
        final ExpandableArrayBuffer buffer = new ExpandableArrayBuffer(1024);

        final MessageWithInvalidGroupEncodingEncoder encoder = new MessageWithInvalidGroupEncodingEncoder();
        encoder.wrapAndApplyHeader(buffer, 0, new MessageHeaderEncoder());
        encoder.id(12345L);

        final MessageWithInvalidGroupEncodingEncoder.PeopleEncoder peopleEncoder = encoder.peopleCount(2);
        peopleEncoder.next();
        peopleEncoder.firstName("Alice");
        peopleEncoder.next();
        peopleEncoder.firstName("Bob");

        final MessageWithInvalidGroupEncodingDecoder decoder = new MessageWithInvalidGroupEncodingDecoder();
        decoder.wrapAndApplyHeader(buffer, 0, new MessageHeaderDecoder());

        assertEquals(12345L, decoder.id());

        MessageWithInvalidGroupEncodingDecoder.PeopleDecoder peopleDecoder = decoder.people();

        peopleDecoder.next();
        assertEquals("Alice", peopleDecoder.firstName());

        peopleDecoder.next();
        assertEquals("Bob", peopleDecoder.firstName());
    }
}
