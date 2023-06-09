// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal;

import com.terraforged.cereal.serial.DataReader;
import com.terraforged.cereal.serial.DataWriter;
import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpecs;
import com.terraforged.cereal.spec.SpecName;
import com.terraforged.cereal.spec.SubSpec;
import com.terraforged.cereal.value.DataList;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Cereal
{
    public static <T> T read(final Reader reader, final Class<T> type) throws IOException {
        final DataValue data = new DataReader(reader).read();
        return deserialize(data.asObj(), type, Context.NONE);
    }
    
    public static <T> T read(final Reader reader, final Class<T> type, final Context context) throws IOException {
        final DataValue data = new DataReader(reader).read();
        return deserialize(data.asObj(), type, context);
    }
    
    public static <T> List<T> readList(final Reader reader, final Class<T> type) throws IOException {
        final DataValue data = new DataReader(reader).read();
        return deserialize(data.asList(), type, Context.NONE);
    }
    
    public static <T> List<T> readList(final Reader reader, final Class<T> type, final Context context) throws IOException {
        final DataValue data = new DataReader(reader).read();
        return deserialize(data.asList(), type, context);
    }
    
    public static void write(final Object object, final Writer writer) throws IOException {
        write(object, writer, Context.NONE);
    }
    
    public static void write(final Object object, final Writer writer, final Context context) throws IOException {
        final DataWriter dataWriter = new DataWriter(writer);
        final DataValue value = serialize(object, context);
        dataWriter.write(value);
    }
    
    public static void write(final Object object, final String type, final Writer writer) throws IOException {
        write(object, type, writer, Context.NONE);
    }
    
    public static void write(final Object object, final String type, final Writer writer, final Context context) throws IOException {
        final DataWriter dataWriter = new DataWriter(writer);
        final DataValue value = serialize(type, object, context);
        dataWriter.write(value);
    }
    
    public static DataValue serialize(final Object value) {
        return serialize(value, Context.NONE);
    }
    
    public static DataValue serialize(final Object value, final Context context) {
        return serializeInferred(value, context);
    }
    
    public static DataValue serialize(final String type, final Object value) {
        return serialize(type, value, Context.NONE);
    }
    
    public static DataValue serialize(final String type, final Object value, final Context context) {
        if (DataSpecs.hasSpec(type)) {
            return DataSpecs.getSpec(type).serialize(value, context);
        }
        if (DataSpecs.isSubSpec(value)) {
            final SubSpec spec = DataSpecs.getSubSpec(value);
            return spec.serialize(value, context);
        }
        return DataValue.of(value, context);
    }
    
    private static DataValue serializeInferred(final Object value, final Context context) {
        if (value.getClass().isArray()) {
            final int size = Array.getLength(value);
            final DataList list = new DataList();
            for (int i = 0; i < size; ++i) {
                list.add(serializeInferred(Array.get(value, i), context));
            }
            return list;
        }
        if (value instanceof Iterable) {
            final DataList list2 = new DataList();
            for (final Object child : (Iterable)value) {
                list2.add(serializeInferred(child, context));
            }
            return list2;
        }
        if (value instanceof Map) {
            final DataObject object = new DataObject();
            for (final Map.Entry entry : ((Map<String, DataValue>)value).entrySet()) {
                if (entry.getKey() instanceof String) {
                    final String key = entry.getKey().toString();
                    final DataValue child2 = serializeInferred(entry.getValue(), context);
                    object.add(key, child2);
                }
            }
            return object;
        }
        if (value instanceof SpecName) {
            final String name = ((SpecName)value).getSpecName();
            if (DataSpecs.hasSpec(name)) {
                return DataSpecs.getSpec(name).serialize(value, context);
            }
        }
        if (DataSpecs.isSubSpec(value)) {
            final SubSpec spec = DataSpecs.getSubSpec(value);
            return spec.serialize(value, context);
        }
        return DataValue.of(value, context);
    }
    
    public static <T> T deserialize(final DataObject data, final Class<T> type, final Context context) {
        final String spec = data.getType();
        if (DataSpecs.hasSpec(spec)) {
            return DataSpecs.getSpec(spec).deserialize(data, type, context);
        }
        final SubSpec<?> subSpec = DataSpecs.getSubSpec(type);
        if (subSpec == null) {
            throw new RuntimeException(String.format("No spec registered for name: '%s' or type: '%s'", spec, type));
        }
        return type.cast(subSpec.deserialize(data, context));
    }
    
    public static <T> List<T> deserialize(final DataList data, final Class<T> type) {
        return deserialize(data, type, Context.NONE);
    }
    
    public static <T> List<T> deserialize(final DataList data, final Class<T> type, final Context context) {
        final List<T> list = new ArrayList<T>(data.size());
        for (final DataValue value : data) {
            if (value.isObj()) {
                list.add(deserialize(value.asObj(), type, context));
            }
        }
        return list;
    }
}
