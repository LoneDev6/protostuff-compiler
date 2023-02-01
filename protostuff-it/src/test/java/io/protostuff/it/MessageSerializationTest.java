package io.protostuff.it;

import io.protostuff.JsonIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.it.message_test.SimpleMessage;
import io.protostuff.it.message_test.TestMap;
import io.protostuff.it.message_test.TestMessage;
import io.protostuff.it.message_test.TestOneof;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class MessageSerializationTest {

    private static final Schema<SimpleMessage> SCHEMA = SimpleMessage.SCHEMA;

    private static final String SCALARS_JSON = "{\"int32\":42,\"string\":\"xxx\"}";
    private static final SimpleMessage SCALARS_MESSAGE = SimpleMessage.create()
            .setInt32(42)
            .setString("xxx");
    private static final String REPEATED_JSON = "{\"repeatedInt32\":[42,43],\"repeatedString\":[\"line1\",\"line2\"]}";
    private static final SimpleMessage REPEATED_MESSAGE = SimpleMessage.create()
            .addRepeatedInt32(42)
            .addRepeatedInt32(43)
            .addRepeatedString("line1")
            .addRepeatedString("line2");
    private static final String NESTED_JSON = "{\"message\":{\"a\":3}}";
    private static final SimpleMessage NESTED_MESSAGE = SimpleMessage.create()
            .setMessage(TestMessage.create()
            .setA(3));

    @Test
    public void scalars_serialize() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonIOUtil.writeTo(stream, SCALARS_MESSAGE, SCHEMA, false);
        String json = new String(stream.toByteArray());
        Assert.assertEquals(SCALARS_JSON, json);
    }

    @Test
    public void scalars_deserialize() throws Exception {
        SimpleMessage result = SCHEMA.newMessage();
        JsonIOUtil.mergeFrom(SCALARS_JSON.getBytes(), result, SCHEMA, false);
        Assert.assertEquals(SCALARS_MESSAGE, result);
    }

    @Test
    public void repeated_serialize() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonIOUtil.writeTo(stream, REPEATED_MESSAGE, SCHEMA, false);
        String json = new String(stream.toByteArray());
        Assert.assertEquals(REPEATED_JSON, json);
    }

    @Test
    public void repeated_deserialize() throws Exception {
        SimpleMessage result = SCHEMA.newMessage();
        JsonIOUtil.mergeFrom(REPEATED_JSON.getBytes(), result, SCHEMA, false);
        Assert.assertEquals(REPEATED_MESSAGE, result);
    }

    @Test
    public void nested_serialize() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonIOUtil.writeTo(stream, NESTED_MESSAGE, SCHEMA, false);
        String json = new String(stream.toByteArray());
        Assert.assertEquals(NESTED_JSON, json);
    }

    @Test
    public void nested_deserialize() throws Exception {
        SimpleMessage result = SCHEMA.newMessage();
        JsonIOUtil.mergeFrom(NESTED_JSON.getBytes(), result, SCHEMA, false);
        Assert.assertEquals(NESTED_MESSAGE, result);
    }

    @Test
    public void map_serialization_deserialization() throws Exception {
        SimpleMessage simpleMessage = SimpleMessage.create()
                .setInt32(5);
        TestMap container = TestMap.create()
                .putMapBoolBool(true, true)
                .putMapBoolBool(false, false)
                .putMapInt32Int32(43, 42)
                .putMapInt32Int32(1, 0)
                .putMapStringSimpleMessage("key", simpleMessage);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ProtobufIOUtil.writeTo(stream, container, TestMap.SCHEMA, LinkedBuffer.allocate());
        byte[] bytes = stream.toByteArray();
        TestMap newInstance = TestMap.SCHEMA.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, newInstance, TestMap.SCHEMA);
        Assert.assertEquals(true, newInstance.getMapBoolBool(true));
        Assert.assertEquals(false, newInstance.getMapBoolBool(false));
        Assert.assertEquals(42, newInstance.getMapInt32Int32(43).intValue());
        Assert.assertEquals(0, newInstance.getMapInt32Int32(1).intValue());
        Assert.assertEquals(simpleMessage, newInstance.getMapStringSimpleMessage("key"));
        Assert.assertEquals(container, newInstance);
    }

    @Test
    public void testOneof_serialization_deserialization() throws Exception {
        TestOneof a = TestOneof.create()
                .setFooString("abra");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ProtobufIOUtil.writeTo(stream, a, TestOneof.SCHEMA, LinkedBuffer.allocate());
        byte[] bytes = stream.toByteArray();
        TestOneof newInstance = TestOneof.SCHEMA.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, newInstance, TestOneof.SCHEMA);
        Assert.assertEquals(a, newInstance);
    }
}
