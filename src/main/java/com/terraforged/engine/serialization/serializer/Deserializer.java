//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.serialization.serializer;

import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Serializable;
import com.terraforged.engine.serialization.annotation.legacy.LegacyBool;
import com.terraforged.engine.serialization.annotation.legacy.LegacyFloat;
import com.terraforged.engine.serialization.annotation.legacy.LegacyInt;
import com.terraforged.engine.serialization.annotation.legacy.LegacyString;
import com.terraforged.noise.util.NoiseUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Deserializer {
    private static final BiFunction<Reader, String, Integer> INT_GETTER = Reader::getInt;
    private static final BiFunction<Reader, String, Float> FLOAT_GETTER = Reader::getFloat;
    private static final BiFunction<Reader, String, String> STRING_GETTER = Reader::getString;
    private static final BiFunction<Reader, String, Boolean> BOOLEAN_GETTER = Reader::getBool;

    public Deserializer() {
    }

    public static boolean deserialize(Reader reader, Object object) throws Throwable {
        boolean valid = true;
        Class<?> type = object.getClass();

        for(Field field : type.getFields()) {
            if (Serializer.isSerializable(field)) {
                field.setAccessible(true);

                try {
                    valid &= fromValue(reader, object, field);
                } catch (Throwable var9) {
                    var9.printStackTrace();
                }
            }
        }

        return valid;
    }

    private static boolean fromValue(Reader reader, Object object, Field field) throws Throwable {
        if (field.getType() == Integer.TYPE) {
            return set(object, field, reader, INT_GETTER, LegacyInt.GETTER);
        } else if (field.getType() == Float.TYPE) {
            return set(object, field, reader, FLOAT_GETTER, LegacyFloat.GETTER);
        } else if (field.getType() == Boolean.TYPE) {
            return set(object, field, reader, BOOLEAN_GETTER, LegacyBool.GETTER);
        } else if (field.getType() == String.class) {
            return set(object, field, reader, STRING_GETTER, LegacyString.GETTER);
        } else if (field.getType().isEnum()) {
            return setEnum(object, field, reader);
        } else if (field.getType().isAnnotationPresent(Serializable.class)) {
            return setObject(object, field, reader);
        } else if (field.getType().isArray()) {
            return setArray(object, field, reader);
        } else {
            if (Map.class.isAssignableFrom(field.getType())) {
                Class<?> valueType = Serializer.getMapValueType(field);
                if (valueType != null && valueType.isAnnotationPresent(Serializable.class)) {
                    return setMap(object, field, valueType, reader);
                }
            }

            return true;
        }
    }

    private static <T> boolean set(Object owner, Field field, Reader reader, BiFunction<Reader, String, T> getter, Function<Field, T> legacy) throws IllegalAccessException {
        T value = (T)(reader.has(field.getName()) ? getter.apply(reader, field.getName()) : legacy.apply(field));
        if (value == null) {
            return false;
        } else if (value instanceof Number) {
            return set(owner, field, clamp((Number)value, field));
        } else {
            field.set(owner, value);
            return true;
        }
    }

    private static boolean setEnum(Object object, Field field, Reader reader) throws IllegalAccessException {
        String name = reader.has(field.getName()) ? reader.getString(field.getName()) : (String)LegacyString.GETTER.apply(field);
        if (name == null) {
            return false;
        } else {
            for(Enum<?> e : (Enum[])field.getType().asSubclass(Enum.class).getEnumConstants()) {
                if (e.name().equals(name)) {
                    field.set(object, e);
                    return true;
                }
            }

            return false;
        }
    }

    private static boolean setObject(Object object, Field field, Reader reader) throws Throwable {
        if (reader.has(field.getName())) {
            Reader child = reader.getChild(field.getName());
            Object value = field.getType().newInstance();
            deserialize(child, value);
            field.set(object, value);
            return true;
        } else {
            return false;
        }
    }

    private static boolean setMap(Object object, Field field, Class<?> valueType, Reader reader) throws Throwable {
        if (reader.has(field.getName())) {
            Map map = (Map)field.get(object);
            map.clear();
            Reader child = reader.getChild(field.getName());

            for(String key : child.getKeys()) {
                if (key.charAt(0) != '#') {
                    Object value = valueType.newInstance();
                    deserialize(child.getChild(key), value);
                    map.put(key, value);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static boolean setArray(Object object, Field field, Reader reader) throws Throwable {
        if (reader.has(field.getName())) {
            Class<?> type = field.getType().getComponentType();
            if (type.isAnnotationPresent(Serializable.class)) {
                Reader child = reader.getChild(field.getName());
                Object array = Array.newInstance(type, child.getSize());

                for(int i = 0; i < child.getSize(); ++i) {
                    Object value = type.newInstance();
                    deserialize(child.getChild(i), value);
                    Array.set(array, i, value);
                }

                field.set(object, array);
                return true;
            }
        }

        return false;
    }

    private static Number clamp(Number value, Field field) {
        Range range = (Range)field.getAnnotation(Range.class);
        return (Number)(range != null ? NoiseUtil.clamp(value.floatValue(), range.min(), range.max()) : value);
    }

    private static boolean set(Object owner, Field field, Number value) throws IllegalAccessException {
        if (field.getType() == Integer.TYPE) {
            field.set(owner, value.intValue());
            return true;
        } else if (field.getType() == Float.TYPE) {
            field.set(owner, value.floatValue());
            return true;
        } else if (field.getType() == Long.TYPE) {
            field.set(owner, value.longValue());
            return true;
        } else if (field.getType() == Double.TYPE) {
            field.set(owner, value.doubleValue());
            return true;
        } else {
            return false;
        }
    }
}
