// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.serializer;

import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Serializable;
import com.terraforged.noise.util.NoiseUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Deserializer
{
    private static final BiFunction<Reader, String, Integer> INT_GETTER;
    private static final BiFunction<Reader, String, Float> FLOAT_GETTER;
    private static final BiFunction<Reader, String, String> STRING_GETTER;
    private static final BiFunction<Reader, String, Boolean> BOOLEAN_GETTER;
    
    public static boolean deserialize(final Reader reader, final Object object) throws Throwable {
        boolean valid = true;
        final Class<?> type = object.getClass();
        for (final Field field : type.getFields()) {
            if (Serializer.isSerializable(field)) {
                field.setAccessible(true);
                try {
                    valid &= fromValue(reader, object, field);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        return valid;
    }
    
    private static boolean fromValue(final Reader reader, final Object object, final Field field) throws Throwable {
        if (field.getType() == Integer.TYPE) {
            return set(object, field, reader, Deserializer.INT_GETTER, (f) -> null);
        }
        if (field.getType() == Float.TYPE) {
            return set(object, field, reader, Deserializer.FLOAT_GETTER, (f) -> null);
        }
        if (field.getType() == Boolean.TYPE) {
            return set(object, field, reader, Deserializer.BOOLEAN_GETTER, (f) -> null);
        }
        if (field.getType() == String.class) {
            return set(object, field, reader, Deserializer.STRING_GETTER, (f) -> null);
        }
        if (field.getType().isEnum()) {
            return setEnum(object, field, reader);
        }
        if (field.getType().isAnnotationPresent(Serializable.class)) {
            return setObject(object, field, reader);
        }
        if (field.getType().isArray()) {
            return setArray(object, field, reader);
        }
        if (Map.class.isAssignableFrom(field.getType())) {
            final Class<?> valueType = Serializer.getMapValueType(field);
            if (valueType != null && valueType.isAnnotationPresent(Serializable.class)) {
                return setMap(object, field, valueType, reader);
            }
        }
        return true;
    }
    
    private static <T> boolean set(final Object owner, final Field field, final Reader reader, final BiFunction<Reader, String, T> getter, final Function<Field, T> legacy) throws IllegalAccessException {
        final T value = reader.has(field.getName()) ? getter.apply(reader, field.getName()) : legacy.apply(field);
        if (value == null) {
            return false;
        }
        if (value instanceof Number) {
            return set(owner, field, clamp((Number)value, field));
        }
        field.set(owner, value);
        return true;
    }
    
    private static boolean setEnum(final Object object, final Field field, final Reader reader) throws IllegalAccessException {
        final String name = reader.has(field.getName()) ? reader.getString(field.getName()) : null;
        if (name == null) {
            return false;
        }
        for (final Enum<?> e : (Enum[])field.getType().asSubclass(Enum.class).getEnumConstants()) {
            if (e.name().equals(name)) {
                field.set(object, e);
                return true;
            }
        }
        return false;
    }
    
    private static boolean setObject(final Object object, final Field field, final Reader reader) throws Throwable {
        if (reader.has(field.getName())) {
            final Reader child = reader.getChild(field.getName());
            final Object value = field.getType().newInstance();
            deserialize(child, value);
            field.set(object, value);
            return true;
        }
        return false;
    }
    
    private static boolean setMap(final Object object, final Field field, final Class<?> valueType, final Reader reader) throws Throwable {
        if (reader.has(field.getName())) {
            final Map map = (Map)field.get(object);
            map.clear();
            final Reader child = reader.getChild(field.getName());
            for (final String key : child.getKeys()) {
                if (key.charAt(0) == '#') {
                    continue;
                }
                final Object value = valueType.newInstance();
                deserialize(child.getChild(key), value);
                map.put(key, value);
            }
            return true;
        }
        return false;
    }
    
    private static boolean setArray(final Object object, final Field field, final Reader reader) throws Throwable {
        if (reader.has(field.getName())) {
            final Class<?> type = field.getType().getComponentType();
            if (type.isAnnotationPresent(Serializable.class)) {
                final Reader child = reader.getChild(field.getName());
                final Object array = Array.newInstance(type, child.getSize());
                for (int i = 0; i < child.getSize(); ++i) {
                    final Object value = type.newInstance();
                    deserialize(child.getChild(i), value);
                    Array.set(array, i, value);
                }
                field.set(object, array);
                return true;
            }
        }
        return false;
    }
    
    private static Number clamp(final Number value, final Field field) {
        final Range range = field.getAnnotation(Range.class);
        if (range != null) {
            return NoiseUtil.clamp(value.floatValue(), range.min(), range.max());
        }
        return value;
    }
    
    private static boolean set(final Object owner, final Field field, final Number value) throws IllegalAccessException {
        if (field.getType() == Integer.TYPE) {
            field.set(owner, value.intValue());
            return true;
        }
        if (field.getType() == Float.TYPE) {
            field.set(owner, value.floatValue());
            return true;
        }
        if (field.getType() == Long.TYPE) {
            field.set(owner, value.longValue());
            return true;
        }
        if (field.getType() == Double.TYPE) {
            field.set(owner, value.doubleValue());
            return true;
        }
        return false;
    }
    
    static {
        INT_GETTER = Reader::getInt;
        FLOAT_GETTER = Reader::getFloat;
        STRING_GETTER = Reader::getString;
        BOOLEAN_GETTER = Reader::getBool;
    }
}
