package io.protostuff.it;

import io.protostuff.it.message_test.*;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static io.protostuff.it.message_test.TestOneof.OneofNameCase.FOO_INT;
import static io.protostuff.it.message_test.TestOneof.OneofNameCase.ONEOF_NAME_NOT_SET;
import static org.junit.Assert.*;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class MessageTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public static final SimpleMessage A = SimpleMessage.create()
            .setInt32(42)
            .setString("abra");
    public static final SimpleMessage A_COPY = SimpleMessage.create()
            .setInt32(42)
            .setString("abra");
    public static final SimpleMessage B = SimpleMessage.create()
            .setInt32(43)
            .setString("cadabra");
    public static final TestMap TEST_MAP = TestMap.create()
            .putMapStringString("key", "value")
            .putMapStringString("test", "test");

    @Test
    public void createdInstance() throws Exception {
        ParentMsg instance = ParentMsg.create()
                .setNestedMsg(NestedMsg.create()
                        .setName("1"))
                .addNestedRepeatedMsg(NestedMsg.create()
                        .setName("2"))
                ;
        assertTrue(instance.hasNestedMsg());
        assertEquals("1", instance.getNestedMsg().getName());
        assertEquals(1, instance.getNestedRepeatedMsgCount());
        assertEquals("2", instance.getNestedRepeatedMsg(0).getName());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(A, A_COPY);
        assertNotEquals(A, B);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(A.hashCode(), A_COPY.hashCode());
        assertNotEquals(A.hashCode(), B.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("SimpleMessage{int32=42, string=abra}", A.toString());
    }

    @Test
    public void testToString_integer_field() throws Exception {
        SimpleMessage message = SimpleMessage.create()
                .setInt32(15)
                ;
        assertEquals("SimpleMessage{int32=15}", message.toString());
    }

    @Test
    public void testToString_string_field() throws Exception {
        SimpleMessage message = SimpleMessage.create()
                .setString("test")
                ;
        assertEquals("SimpleMessage{string=test}", message.toString());
    }

    @Test
    public void testToString_message_field() throws Exception {
        SimpleMessage message = SimpleMessage.create()
                .setMessage(TestMessage.create()
                        .setA(123)
                        )
                ;
        assertEquals("SimpleMessage{message=TestMessage{a=123}}", message.toString());
    }

    @Test
    public void testToString_repeated_string_field() throws Exception {
        SimpleMessage message = SimpleMessage.create()
                .addRepeatedString("test1")
                .addRepeatedString("test2")
                ;
        assertEquals("SimpleMessage{repeatedString=[test1, test2]}", message.toString());
    }

    @Test
    public void testToString_repeated_int32_field() throws Exception {
        SimpleMessage message = SimpleMessage.create()
                .addRepeatedInt32(41)
                .addRepeatedInt32(42)
                ;
        assertEquals("SimpleMessage{repeatedInt32=[41, 42]}", message.toString());
    }

    @Test
    public void testToString_MessageWithoutFields() throws Exception {
        MessageWithoutFields message = MessageWithoutFields.create();
        assertEquals("MessageWithoutFields{}", message.toString());
    }

    @Test
    public void testMap_getter_map() throws Exception {
        Map<String, String> expected = new HashMap<>();
        expected.put("key", "value");
        expected.put("test", "test");
        Assert.assertEquals(expected, TEST_MAP.getMapStringString());
    }

    @Test
    public void testMap_getter_single() throws Exception {
        Assert.assertEquals("value", TEST_MAP.getMapStringString("key"));
    }

    @Test
    public void testMap_getter_count() throws Exception {
        Assert.assertEquals(2, TEST_MAP.getMapStringStringCount());
    }

    @Test
    public void testMap_setter_map() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        map.put("test", "test");
        TestMap instance = TestMap.create()
                .setMapStringString(map)
                ;
        Assert.assertEquals(map, instance.getMapStringString());
    }

    @Test
    public void testOneof_default_value() throws Exception {
        TestOneof testOneof = TestOneof.create();
        assertEquals(ONEOF_NAME_NOT_SET, testOneof.getOneofNameCase());
        assertFalse(testOneof.hasFooInt());
        assertFalse(testOneof.hasFooString());
        assertEquals(0, testOneof.getFooInt());
        assertEquals(null, testOneof.getFooString());
    }

    @Test
    public void testOneof_set_value() throws Exception {
        TestOneof testOneof = TestOneof.create()
                .setFooInt(42)
                ;
        assertEquals(FOO_INT, testOneof.getOneofNameCase());
        assertTrue(testOneof.hasFooInt());
        assertFalse(testOneof.hasFooString());
        assertEquals(42, testOneof.getFooInt());
        assertEquals(null, testOneof.getFooString());
    }

    @Test
    public void testOneof_equals() throws Exception {
        TestOneof a1 = TestOneof.create()
                .setFooInt(42)
                ;
        TestOneof a2 = TestOneof.create()
                .setFooInt(42)
                ;
        TestOneof b = TestOneof.create()
                .setFooString("test")
                ;
        assertEquals(a1, a2);
        assertNotEquals(a1, b);
    }

    @Test
    public void testOneof_hashCode() throws Exception {
        TestOneof a1 = TestOneof.create()
                .setFooInt(42)
                ;
        TestOneof a2 = TestOneof.create()
                .setFooInt(42)
                ;
        TestOneof b = TestOneof.create()
                .setFooString("test")
                ;
        assertEquals(a1.hashCode(), a2.hashCode());
        assertNotEquals(a1.hashCode(), b.hashCode());
    }

    @Test
    public void testOneof_toString() throws Exception {
        TestOneof a1 = TestOneof.create()
                .setFooInt(42)
                ;
        assertEquals("TestOneof{fooInt=42}", a1.toString());
    }

}
