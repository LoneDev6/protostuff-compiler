package io.protostuff.generator.java;

import io.protostuff.compiler.model.*;
import io.protostuff.compiler.model.Enum;
import io.protostuff.generator.Formatter;

import static io.protostuff.compiler.model.ScalarFieldType.BOOL;
import static io.protostuff.compiler.model.ScalarFieldType.BYTES;
import static io.protostuff.compiler.model.ScalarFieldType.STRING;
import static io.protostuff.compiler.parser.MessageParseListener.MAP_ENTRY_KEY;
import static io.protostuff.compiler.parser.MessageParseListener.MAP_ENTRY_VALUE;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class MessageFieldUtil {

    public static final String HAS_PREFIX = "has";
    public static final String GETTER_PREFIX = "get";
    public static final String SETTER_PREFIX = "set";
    public static final String LIST = "java.util.List";
    public static final String NULL = "null";
    public static final String PUT_PREFIX = "put";
    public static final String ADD_PREFIX = "add";
    public static final String REMOVE_PREFIX = "remove";

    public static String getFieldType(Field field) {
        FieldType type = field.getType();
        if (type instanceof ScalarFieldType) {
            ScalarFieldType scalarFieldType = (ScalarFieldType) type;
            return ScalarFieldTypeUtil.getPrimitiveType(scalarFieldType);
        }
        if (type instanceof UserType) {
            UserType userType = (UserType) type;
            return UserTypeUtil.getCanonicalName(userType);
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String getFieldName(Field field) {
        String name = field.getName();
        String formattedName = Formatter.toCamelCase(name);
        if (isReservedKeyword(formattedName)) {
            return formattedName + '_';
        }
        return formattedName;
    }

    private static boolean isReservedKeyword(String formattedName) {
        return JavaConstants.RESERVED_KEYWORDS.contains(formattedName);
    }

    public static String getFieldGetterName(Field field) {
        return GETTER_PREFIX + Formatter.toPascalCase(field.getName());
    }

    public static String getFieldSetterName(Field field) {
        return SETTER_PREFIX + Formatter.toPascalCase(field.getName());
    }

    public static boolean isMessage(Field field) {
        return field.getType() instanceof Message;
    }

    public static String getHasMethodName(Field field) {
        return HAS_PREFIX + Formatter.toPascalCase(field.getName());
    }

    public static String getBuilderSetterName(Field field) {
        return SETTER_PREFIX + Formatter.toPascalCase(field.getName());
    }

    public static String getDefaultValue(Field field) {
        FieldType type = field.getType();
        if (type instanceof ScalarFieldType) {
            return ScalarFieldTypeUtil.getDefaultValue((ScalarFieldType) type);
        }
        if (type instanceof Message) {
            return NULL;
        }
        if (type instanceof Enum) {
            Enum anEnum = (Enum) type;
            return UserTypeUtil.getCanonicalName(anEnum) + "." + anEnum.getConstants().get(0).getName();
        }
        throw new IllegalArgumentException(String.valueOf(type));
    }

    /**
     * Check if field type used to store value in java is nullable type.
     */
    public static boolean isScalarNullableType(Field field) {
        FieldType type = field.getType();
        return STRING.equals(type) || BYTES.equals(type) || type instanceof io.protostuff.compiler.model.Enum;
    }

    public static String getRepeatedFieldType(Field field) {
        FieldType type = field.getType();
        if (type instanceof ScalarFieldType) {
            ScalarFieldType scalarFieldType = (ScalarFieldType) type;
            return LIST + "<" + ScalarFieldTypeUtil.getWrapperType(scalarFieldType) + ">";
        }
        if (type instanceof UserType) {
            UserType userType = (UserType) type;
            return LIST + "<" + UserTypeUtil.getCanonicalName(userType) + ">";
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String getRepeatedFieldGetterName(Field field) {
        if (field.isRepeated()) {
            return GETTER_PREFIX + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String getRepeatedFieldSetterName(Field field) {
        if (field.isRepeated()) {
            return SETTER_PREFIX + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String repeatedGetCountMethodName(Field field) {
        if (field.isRepeated()) {
            return GETTER_PREFIX + Formatter.toPascalCase(field.getName()) + "Count";
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String repeatedGetByIndexMethodName(Field field) {
        if (field.isRepeated()) {
            return GETTER_PREFIX + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String getRepeatedBuilderSetterName(Field field) {
        return SETTER_PREFIX + Formatter.toPascalCase(field.getName()) + "List";
    }

    public static String getBuilderGetterName(Field field) {
        return GETTER_PREFIX + Formatter.toPascalCase(field.getName());
    }

    public static String getRepeatedFieldAdderName(Field field) {
        return ADD_PREFIX + Formatter.toPascalCase(field.getName());
    }

    public static String toStringPart(Field field) {
        String getterName;
        if (field.isMap()) {
            getterName = getMapGetterName(field);
        } else if (field.isRepeated()) {
            getterName = getRepeatedFieldGetterName(field);
        } else {
            getterName = getFieldGetterName(field);
        }
        return "\"" + getFieldName(field) + "=\" + " + getterName + "()";
    }

    public static String protostuffReadMethod(Field field) {
        FieldType type = field.getType();
        if (!(type instanceof ScalarFieldType)) {
            throw new IllegalArgumentException(String.valueOf(type));
        }
        ScalarFieldType fieldType = (ScalarFieldType) type;
        String name;
        switch (fieldType) {
            case INT32:
                name = "readInt32";
                break;
            case INT64:
                name = "readInt64";
                break;
            case UINT32:
                name = "readUInt32";
                break;
            case UINT64:
                name = "readUInt64";
                break;
            case SINT32:
                name = "readSInt32";
                break;
            case SINT64:
                name = "readSInt64";
                break;
            case FIXED32:
                name = "readFixed32";
                break;
            case FIXED64:
                name = "readFixed64";
                break;
            case SFIXED32:
                name = "readSFixed32";
                break;
            case SFIXED64:
                name = "readSFixed64";
                break;
            case FLOAT:
                name = "readFloat";
                break;
            case DOUBLE:
                name = "readDouble";
                break;
            case BOOL:
                name = "readBool";
                break;
            case STRING:
                name = "readString";
                break;
            case BYTES:
                name = "readBytes";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(type));
        }
        return name;
    }

    public static String protostuffWriteMethod(Field field) {
        FieldType type = field.getType();
        if (!(type instanceof ScalarFieldType)) {
            throw new IllegalArgumentException(String.valueOf(type));
        }
        ScalarFieldType fieldType = (ScalarFieldType) type;
        String name;
        switch (fieldType) {
            case INT32:
                name = "writeInt32";
                break;
            case INT64:
                name = "writeInt64";
                break;
            case UINT32:
                name = "writeUInt32";
                break;
            case UINT64:
                name = "writeUInt64";
                break;
            case SINT32:
                name = "writeSInt32";
                break;
            case SINT64:
                name = "writeSInt64";
                break;
            case FIXED32:
                name = "writeFixed32";
                break;
            case FIXED64:
                name = "writeFixed64";
                break;
            case SFIXED32:
                name = "writeSFixed32";
                break;
            case SFIXED64:
                name = "writeSFixed64";
                break;
            case FLOAT:
                name = "writeFloat";
                break;
            case DOUBLE:
                name = "writeDouble";
                break;
            case BOOL:
                name = "writeBool";
                break;
            case STRING:
                name = "writeString";
                break;
            case BYTES:
                name = "writeBytes";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(type));
        }
        return name;
    }

    public static String bitFieldName(Field field) {
        return "__bitField" + (field.getIndex() - 1) / 32;
    }

    public static int bitFieldIndex(Field field) {
        return (field.getIndex() - 1) % 32;
    }

    public static int bitFieldMask(Field field) {
        return 1 << bitFieldIndex(field);
    }

    public static String getMapFieldType(Field field) {
        String k = getMapFieldKeyType(field);
        String v = getMapFieldValueType(field);
        return "java.util.Map<" + k + ", " + v + ">";
    }

    public static String getMapFieldKeyType(Field field) {
        FieldType type = field.getType();
        if (!(type instanceof Message)) {
            throw new IllegalArgumentException(field.toString());
        }
        Message entryType = (Message) type;
        ScalarFieldType keyType = (ScalarFieldType) entryType.getField(MAP_ENTRY_KEY).getType();
        return ScalarFieldTypeUtil.getWrapperType(keyType);
    }

    public static String getMapFieldValueType(Field field) {
        FieldType type = field.getType();
        if (!(type instanceof Message)) {
            throw new IllegalArgumentException(field.toString());
        }
        Message entryType = (Message) type;
        Type valueType = entryType.getField(MAP_ENTRY_VALUE).getType();
        String v;
        if (valueType instanceof ScalarFieldType) {
            ScalarFieldType vType = (ScalarFieldType) valueType;
            v= ScalarFieldTypeUtil.getWrapperType(vType);
        } else {
            UserType userType = (UserType) valueType;
            v = UserTypeUtil.getCanonicalName(userType);
        }
        return v;
    }

    public static String getMapGetterName(Field field) {
        if (field.isMap()) {
            return GETTER_PREFIX + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String getMapSetterName(Field field) {
        if (field.isMap()) {
            return SETTER_PREFIX + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String mapGetByKeyMethodName(Field field) {
        if (field.isMap()) {
            return GETTER_PREFIX + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String mapContainsKeyMethodName(Field field) {
        if (field.isMap()) {
            return HAS_PREFIX + "Key" + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String mapContainsValueMethodName(Field field) {
        if (field.isMap()) {
            return HAS_PREFIX + "Value" + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String mapRemoveMethodName(Field field) {
        if (field.isMap()) {
            return REMOVE_PREFIX + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String getMapFieldAdderName(Field field) {
        if (field.isMap()) {
            return PUT_PREFIX + Formatter.toPascalCase(field.getName());
        }
        throw new IllegalArgumentException(field.toString());
    }

    public static String javaOneofConstantName(Field field) {
        String name = field.getName();
        String underscored = Formatter.toUnderscoreCase(name);
        return Formatter.toUpperCase(underscored);
    }

    public static boolean isNumericType(Field field) {
        FieldType type = field.getType();
        boolean scalar = type instanceof ScalarFieldType;
        return scalar && !(BOOL.equals(type) || STRING.equals(type) || BYTES.equals(type));
    }

    public static boolean isBooleanType(Field field) {
        return BOOL.equals(field.getType());
    }
}
