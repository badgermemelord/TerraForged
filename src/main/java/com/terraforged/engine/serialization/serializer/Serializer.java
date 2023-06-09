// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.serializer;

import com.terraforged.engine.Engine;
import com.terraforged.engine.serialization.annotation.*;
import com.terraforged.engine.util.NameUtil;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Serializer
{
    public static final char META_PREFIX = '#';
    public static final String HIDE = "#hide";
    public static final String KEY = "key";
    public static final String ORDER = "order";
    public static final String DISPLAY = "display";
    public static final String NO_NAME = "noname";
    public static final String COMMENT = "comment";
    public static final String RANDOM = "random";
    public static final String OPTIONS = "options";
    public static final String BOUND_MIN = "min";
    public static final String BOUND_MAX = "max";
    public static final String LINK_PAD = "pad";
    public static final String LINK_LOWER = "limit_lower";
    public static final String LINK_UPPER = "limit_upper";
    public static final String RESTRICTED = "restricted";
    public static final String RESTRICTED_NAME = "name";
    public static final String RESTRICTED_OPTIONS = "options";
    
    public static void serialize(final Object object, final Writer writer) throws IllegalAccessException {
        serialize(object, writer, true);
    }
    
    public static void serialize(final Object object, final Writer writer, final boolean meta) throws IllegalAccessException {
        serialize(object, writer, "", meta);
    }
    
    public static void serialize(final Object object, final Writer writer, final String parentId, final boolean meta) throws IllegalAccessException {
        if (object instanceof Map) {
            serializeMap((Map<?, ?>)object, writer, parentId, meta, false);
        }
        else if (object.getClass().isArray()) {
            writer.beginArray();
            for (int length = Array.getLength(object), i = 0; i < length; ++i) {
                final Object element = Array.get(object, i);
                serialize(element, writer);
            }
            writer.endArray();
        }
        else if (!object.getClass().isPrimitive()) {
            int order = 0;
            writer.beginObject();
            for (final Field field : object.getClass().getFields()) {
                if (isSerializable(field)) {
                    field.setAccessible(true);
                    write(object, field, order, writer, parentId, meta);
                    ++order;
                }
                else if (meta && isHideMarker(field)) {
                    writer.name("#hide").value((boolean)field.get(object));
                }
            }
            writer.endObject();
        }
    }
    
    private static void write(final Object object, final Field field, final int order, final Writer writer, final String parentId, final boolean meta) throws IllegalAccessException {
        if (field.getType() == Integer.TYPE) {
            writer.name(field.getName()).value((int)field.get(object));
            writeMeta(field, order, writer, parentId, meta);
            return;
        }
        if (field.getType() == Float.TYPE) {
            writer.name(field.getName()).value((float)field.get(object));
            writeMeta(field, order, writer, parentId, meta);
            return;
        }
        if (field.getType() == String.class) {
            writer.name(field.getName()).value((String)field.get(object));
            writeMeta(field, order, writer, parentId, meta);
            return;
        }
        if (field.getType() == Boolean.TYPE) {
            writer.name(field.getName()).value((boolean)field.get(object));
            writeMeta(field, order, writer, parentId, meta);
            return;
        }
        if (field.getType().isEnum()) {
            writer.name(field.getName()).value(((Enum)field.get(object)).name());
            writeMeta(field, order, writer, parentId, meta);
            return;
        }
        if (field.getType().isArray()) {
            if (field.getType().getComponentType().isAnnotationPresent(Serializable.class)) {
                writer.name(field.getName());
                serialize(field.get(object), writer, getKeyName(parentId, field), meta);
                writeMeta(field, order, writer, parentId, meta);
            }
            return;
        }
        if (Map.class.isAssignableFrom(field.getType())) {
            final Class<?> valueType = getMapValueType(field);
            if (valueType != null && valueType.isAnnotationPresent(Serializable.class)) {
                writer.name(field.getName());
                serializeMap((Map<?, ?>)field.get(object), writer, parentId, meta, field.isAnnotationPresent(Sorted.class));
                writeMeta(field, order, writer, parentId, meta);
            }
            return;
        }
        if (field.getType().isAnnotationPresent(Serializable.class)) {
            writer.name(field.getName());
            final String parent = getKeyName(parentId, field);
            serialize(field.get(object), writer, parent, meta);
            writeMeta(field, order, writer, parentId, meta);
        }
    }
    
    private static void serializeMap(final Map<?, ?> map, final Writer writer, final String parentId, final boolean meta, final boolean sorted) throws IllegalAccessException {
        writer.beginObject();
        Collection<? extends Map.Entry<?, ?>> entries = map.entrySet();
        if (sorted) {
            entries = entries.stream().sorted(Comparator.comparing(e -> e.getKey().toString())).collect((Collector<? super Map.Entry<?, ?>, ?, Collection<? extends Map.Entry<?, ?>>>)Collectors.toList());
        }
        int order = 0;
        for (final Map.Entry<?, ?> e2 : entries) {
            final String name = e2.getKey().toString();
            writer.name(name);
            serialize(e2.getValue(), writer, parentId, meta);
            writeMapEntryMeta(name, order, writer, meta);
            ++order;
        }
        writer.endObject();
    }
    
    private static void writeMeta(final Field field, final int order, final Writer writer, final String parentId, final boolean meta) throws IllegalAccessException {
        if (!meta) {
            return;
        }
        writer.name('#' + field.getName()).beginObject();
        writer.name("order").value(order);
        writer.name("key").value(getKeyName(parentId, field));
        writer.name("display").value(getDisplayName(field));
        final Range range = field.getAnnotation(Range.class);
        if (range != null) {
            if (field.getType() == Integer.TYPE) {
                writer.name("min").value((int)range.min());
                writer.name("max").value((int)range.max());
            }
            else {
                writer.name("min").value(range.min());
                writer.name("max").value(range.max());
            }
        }
        final Rand seed = field.getAnnotation(Rand.class);
        if (seed != null) {
            writer.name("random").value(1);
        }
        final Comment comment = field.getAnnotation(Comment.class);
        if (comment != null) {
            writer.name("comment");
            writer.value(getComment(comment));
        }
        final NoName noName = field.getAnnotation(NoName.class);
        if (noName != null) {
            writer.name("noname");
            writer.value(true);
        }
        final Limit limit = field.getAnnotation(Limit.class);
        if (limit != null) {
            writer.name("limit_lower");
            writer.value(limit.lower());
            writer.name("limit_upper");
            writer.value(limit.upper());
            writer.name("pad");
            writer.value(limit.pad());
        }
        final Restricted restricted = field.getAnnotation(Restricted.class);
        if (restricted != null) {
            writer.name("restricted");
            writer.beginObject();
            writer.name("name");
            writer.value(restricted.name());
            writer.name("options");
            writer.beginArray();
            for (final String value : restricted.value()) {
                writer.value(value);
            }
            writer.endArray();
            writer.endObject();
        }
        if (field.getType() == Boolean.TYPE) {
            writer.name("options");
            writer.beginArray();
            writer.value(true);
            writer.value(false);
            writer.endArray();
        }
        if (field.getType().isEnum()) {
            writer.name("options");
            writer.beginArray();
            for (final Enum<?> o : (Enum[])field.getType().asSubclass(Enum.class).getEnumConstants()) {
                if (isValidOption(o)) {
                    writer.value(o.name());
                }
            }
            writer.endArray();
        }
        writer.endObject();
    }
    
    private static void writeMapEntryMeta(final String name, final int order, final Writer writer, final boolean meta) {
        if (!meta) {
            return;
        }
        writer.name('#' + name);
        writer.beginObject();
        writer.name("order").value(order);
        writer.name("key").value(name);
        writer.name("display").value(name);
        writer.endObject();
    }
    
    private static String getDisplayName(final Field field) {
        final Name nameMeta = field.getAnnotation(Name.class);
        final String name = (nameMeta == null) ? field.getName() : nameMeta.value();
        return NameUtil.toDisplayName(name);
    }
    
    private static String getKeyName(final String parent, final Field field) {
        final Name nameMeta = field.getAnnotation(Name.class);
        final String name = (nameMeta == null) ? field.getName() : nameMeta.value();
        return NameUtil.toTranslationKey(parent, name);
    }
    
    private static String getComment(final Comment comment) {
        return String.join("\n", (CharSequence[])comment.value());
    }
    
    private static boolean isValidOption(final Enum<?> value) {
        if (Engine.ENFORCE_STABLE_OPTIONS) {
            try {
                final Class<?> type = value.getDeclaringClass();
                final Field field = type.getDeclaredField(value.name());
                return !field.isAnnotationPresent(Unstable.class);
            }
            catch (NoSuchFieldException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
    protected static Class<?> getMapValueType(final Field field) {
        final ParameterizedType genericType = (ParameterizedType)field.getGenericType();
        final Type[] types = genericType.getActualTypeArguments();
        if (types.length == 2) {
            return (Class<?>)types[1];
        }
        return null;
    }
    
    protected static boolean isSerializable(final Field field) {
        final int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }
    
    protected static boolean isHideMarker(final Field field) {
        final int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && Modifier.isTransient(modifiers) && field.getType() == Boolean.TYPE;
    }
}
