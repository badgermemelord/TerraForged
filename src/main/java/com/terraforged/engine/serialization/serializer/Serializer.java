//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.serialization.serializer;

import com.terraforged.engine.Engine;
import com.terraforged.engine.serialization.annotation.Comment;
import com.terraforged.engine.serialization.annotation.Limit;
import com.terraforged.engine.serialization.annotation.Name;
import com.terraforged.engine.serialization.annotation.NoName;
import com.terraforged.engine.serialization.annotation.Rand;
import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Restricted;
import com.terraforged.engine.serialization.annotation.Serializable;
import com.terraforged.engine.serialization.annotation.Sorted;
import com.terraforged.engine.serialization.annotation.Unstable;
import com.terraforged.engine.util.NameUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Serializer {
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

    public Serializer() {
    }

    public static void serialize(Object object, Writer writer) throws IllegalAccessException {
        serialize(object, writer, true);
    }

    public static void serialize(Object object, Writer writer, boolean meta) throws IllegalAccessException {
        serialize(object, writer, "", meta);
    }

    public static void serialize(Object object, Writer writer, String parentId, boolean meta) throws IllegalAccessException {
        if (object instanceof Map) {
            serializeMap((Map<?, ?>)object, writer, parentId, meta, false);
        } else if (object.getClass().isArray()) {
            writer.beginArray();
            int length = Array.getLength(object);

            for(int i = 0; i < length; ++i) {
                Object element = Array.get(object, i);
                serialize(element, writer);
            }

            writer.endArray();
        } else if (!object.getClass().isPrimitive()) {
            int order = 0;
            writer.beginObject();

            for(Field field : object.getClass().getFields()) {
                if (isSerializable(field)) {
                    field.setAccessible(true);
                    write(object, field, order, writer, parentId, meta);
                    ++order;
                } else if (meta && isHideMarker(field)) {
                    writer.name("#hide").value((String) field.get(object));
                }
            }

            writer.endObject();
        }
    }

    private static void write(Object object, Field field, int order, Writer writer, String parentId, boolean meta) throws IllegalAccessException {
        if (field.getType() == Integer.TYPE) {
            writer.name(field.getName()).value((String) field.get(object));
            writeMeta(field, order, writer, parentId, meta);
        } else if (field.getType() == Float.TYPE) {
            writer.name(field.getName()).value((String) field.get(object));
            writeMeta(field, order, writer, parentId, meta);
        } else if (field.getType() == String.class) {
            writer.name(field.getName()).value((String)field.get(object));
            writeMeta(field, order, writer, parentId, meta);
        } else if (field.getType() == Boolean.TYPE) {
            writer.name(field.getName()).value((String) field.get(object));
            writeMeta(field, order, writer, parentId, meta);
        } else if (field.getType().isEnum()) {
            writer.name(field.getName()).value(((Enum)field.get(object)).name());
            writeMeta(field, order, writer, parentId, meta);
        } else if (field.getType().isArray()) {
            if (field.getType().getComponentType().isAnnotationPresent(Serializable.class)) {
                writer.name(field.getName());
                serialize(field.get(object), writer, getKeyName(parentId, field), meta);
                writeMeta(field, order, writer, parentId, meta);
            }
        } else if (Map.class.isAssignableFrom(field.getType())) {
            Class<?> valueType = getMapValueType(field);
            if (valueType != null && valueType.isAnnotationPresent(Serializable.class)) {
                writer.name(field.getName());
                serializeMap((Map<?, ?>)field.get(object), writer, parentId, meta, field.isAnnotationPresent(Sorted.class));
                writeMeta(field, order, writer, parentId, meta);
            }
        } else {
            if (field.getType().isAnnotationPresent(Serializable.class)) {
                writer.name(field.getName());
                String parent = getKeyName(parentId, field);
                serialize(field.get(object), writer, parent, meta);
                writeMeta(field, order, writer, parentId, meta);
            }
        }
    }

    private static void serializeMap(Map<?, ?> map, Writer writer, String parentId, boolean meta, boolean sorted) throws IllegalAccessException {
        writer.beginObject();
        Collection<? extends Entry<?, ?>> entries = map.entrySet();
        if (sorted) {
            entries = (Collection)entries.stream().sorted(Comparator.comparing(ex -> ex.getKey().toString())).collect(Collectors.toList());
        }

        int order = 0;

        for(Entry<?, ?> e : entries) {
            String name = e.getKey().toString();
            writer.name(name);
            serialize(e.getValue(), writer, parentId, meta);
            writeMapEntryMeta(name, order, writer, meta);
            ++order;
        }

        writer.endObject();
    }

    private static void writeMeta(Field field, int order, Writer writer, String parentId, boolean meta) throws IllegalAccessException {
        if (meta) {
            writer.name('#' + field.getName()).beginObject();
            writer.name("order").value(order);
            writer.name("key").value(getKeyName(parentId, field));
            writer.name("display").value(getDisplayName(field));
            Range range = (Range)field.getAnnotation(Range.class);
            if (range != null) {
                if (field.getType() == Integer.TYPE) {
                    writer.name("min").value((int)range.min());
                    writer.name("max").value((int)range.max());
                } else {
                    writer.name("min").value(range.min());
                    writer.name("max").value(range.max());
                }
            }

            Rand seed = (Rand)field.getAnnotation(Rand.class);
            if (seed != null) {
                writer.name("random").value(1);
            }

            Comment comment = (Comment)field.getAnnotation(Comment.class);
            if (comment != null) {
                writer.name("comment");
                writer.value(getComment(comment));
            }

            NoName noName = (NoName)field.getAnnotation(NoName.class);
            if (noName != null) {
                writer.name("noname");
                writer.value(true);
            }

            Limit limit = (Limit)field.getAnnotation(Limit.class);
            if (limit != null) {
                writer.name("limit_lower");
                writer.value(limit.lower());
                writer.name("limit_upper");
                writer.value(limit.upper());
                writer.name("pad");
                writer.value(limit.pad());
            }

            Restricted restricted = (Restricted)field.getAnnotation(Restricted.class);
            if (restricted != null) {
                writer.name("restricted");
                writer.beginObject();
                writer.name("name");
                writer.value(restricted.name());
                writer.name("options");
                writer.beginArray();

                for(String value : restricted.value()) {
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

                for(Enum<?> o : (Enum[])field.getType().asSubclass(Enum.class).getEnumConstants()) {
                    if (isValidOption(o)) {
                        writer.value(o.name());
                    }
                }

                writer.endArray();
            }

            writer.endObject();
        }
    }

    private static void writeMapEntryMeta(String name, int order, Writer writer, boolean meta) {
        if (meta) {
            writer.name('#' + name);
            writer.beginObject();
            writer.name("order").value(order);
            writer.name("key").value(name);
            writer.name("display").value(name);
            writer.endObject();
        }
    }

    private static String getDisplayName(Field field) {
        Name nameMeta = (Name)field.getAnnotation(Name.class);
        String name = nameMeta == null ? field.getName() : nameMeta.value();
        return NameUtil.toDisplayName(name);
    }

    private static String getKeyName(String parent, Field field) {
        Name nameMeta = (Name)field.getAnnotation(Name.class);
        String name = nameMeta == null ? field.getName() : nameMeta.value();
        return NameUtil.toTranslationKey(parent, name);
    }

    private static String getComment(Comment comment) {
        return String.join("\n", comment.value());
    }

    private static boolean isValidOption(Enum<?> value) {
        if (Engine.ENFORCE_STABLE_OPTIONS) {
            try {
                Class<?> type = value.getDeclaringClass();
                Field field = type.getDeclaredField(value.name());
                return !field.isAnnotationPresent(Unstable.class);
            } catch (NoSuchFieldException var3) {
                var3.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    protected static Class<?> getMapValueType(Field field) {
        ParameterizedType genericType = (ParameterizedType)field.getGenericType();
        Type[] types = genericType.getActualTypeArguments();
        return types.length == 2 ? (Class)types[1] : null;
    }

    protected static boolean isSerializable(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }

    protected static boolean isHideMarker(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers)
                && !Modifier.isFinal(modifiers)
                && !Modifier.isStatic(modifiers)
                && Modifier.isTransient(modifiers)
                && field.getType() == Boolean.TYPE;
    }
}
