package io.protostuff.generator.java;

import io.protostuff.compiler.model.Message;
import io.protostuff.compiler.model.Module;
import io.protostuff.compiler.model.Oneof;
import io.protostuff.compiler.model.Proto;
import io.protostuff.generator.Formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class MessageUtil {

    public static boolean hasFields(Message message) {
        return !message.getFields().isEmpty();
    }
    public static boolean isThreadSafe(Message message) {
        Proto proto = message.getProto();
        Module module = proto.getModule();
        List<String> threadSafe = module.getThreadSafe();
        String identifier;
        if(proto.getOptions().containsKey("java_package")) {
            identifier = proto.getOptions().get("java_package").getString();
        } else {
            identifier = proto.getPackage().getValue();
        }
        identifier = identifier + ".";
        if(threadSafe.contains(identifier + message.getName()))
            return true;

        for (String s : threadSafe) {
            if(s.endsWith("*")) {
                if(s.substring(0, s.length() - 1).equals(identifier))
                    return true;
            }
        }

        return false;
    }

    public static List<String> bitFieldNames(Message message) {
        int fieldCount = message.getFieldCount();
        if (fieldCount == 0) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int n = (fieldCount-1) / 32 + 1;
        for (int i = 0; i < n; i++) {
            result.add("__bitField" + i);
        }
        return result;
    }

    public static String getOneofEnumClassName(Oneof oneof) {
        String name = oneof.getName();
        return Formatter.toPascalCase(name) + "Case";
    }

    public static String getOneofNotSetConstantName(Oneof oneof) {
        String name = oneof.getName();
        String underscored = Formatter.toUnderscoreCase(name);
        return Formatter.toUpperCase(underscored) + "_NOT_SET";
    }

    public static String getOneofCaseGetterName(Oneof oneof) {
        String name = oneof.getName();
        return "get" + Formatter.toPascalCase(name) + "Case";
    }

    public static String getOneofFieldName(Oneof oneof) {
        String name = oneof.getName();
        return Formatter.toCamelCase(name) + "__";
    }

    public static String getOneofCaseFieldName(Oneof oneof) {
        String name = oneof.getName();
        return Formatter.toCamelCase(name) + "Case__";
    }
}
