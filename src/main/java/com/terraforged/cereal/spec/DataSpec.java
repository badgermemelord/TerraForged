// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.spec;

import com.terraforged.cereal.Cereal;
import com.terraforged.cereal.value.DataList;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class DataSpec<T>
{
    private final String name;
    private final Class<T> type;
    private final DataFactory<T> constructor;
    private final Map<String, DefaultData> defaults;
    private final Map<String, DataAccessor<T, ?>> accessors;

    public DataSpec(DataSpec.Builder<T> builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.constructor = builder.constructor;
        this.defaults = Collections.unmodifiableMap(builder.defaults);
        this.accessors = Collections.unmodifiableMap(builder.accessors);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<T> getType() {
        return this.type;
    }
    
    public <V> V get(final String key, final DataObject holder, final Function<DataValue, V> accessor) {
        return accessor.apply(this.getValue(key, holder));
    }
    
    public <V> V get(final String key, final DataObject holder, final Class<V> type) {
        return this.get(key, holder, type, Context.NONE);
    }
    
    public <V> V get(final String key, final DataObject holder, final Class<V> type, final Context context) {
        final DataObject value = holder.get(key).asObj();
        return Cereal.deserialize(value, type, context);
    }
    
    public <V extends Enum<V>> V getEnum(final String key, final DataObject holder, final Class<V> type) {
        return Enum.valueOf(type, this.getValue(key, holder).asString());
    }
    
    public <V> List<V> getList(final String key, final DataObject holder, final Class<V> type, final Context context) {
        final DataList list = holder.get(key).asList();
        return Cereal.deserialize(list, type, context);
    }
    
    public <V> Stream<V> getStream(final String key, final DataObject holder, final Class<V> type, final Context context) {
        return this.getList(key, holder, type, context).stream();
    }
    
    public DataValue serialize(final Object value) {
        return this.serialize(value, Context.NONE);
    }
    
    public DataValue serialize(final Object value, final Context context) {
        if (this.getType().isInstance(value)) {
            final boolean skipDefaults = context.skipDefaults();
            final T t = this.getType().cast(value);
            final DataObject root = new DataObject(this.name);
            for (final Map.Entry<String, DataAccessor<T, ?>> e : this.accessors.entrySet()) {
                final Object o = e.getValue().access(t, context);
                final DataValue val = Cereal.serialize(o, context);
                if (skipDefaults && val.equals(this.getDefault(e.getKey()))) {
                    continue;
                }
                root.add(e.getKey(), val);
            }
            return root;
        }
        return DataValue.NULL;
    }
    
    public T deserialize(final DataObject data) {
        return this.deserialize(data, Context.NONE);
    }
    
    public <V> V deserialize(final DataObject data, final Class<V> type) {
        return this.deserialize(data, type, Context.NONE);
    }
    
    public T deserialize(final DataObject data, final Context context) {
        return this.constructor.create(data, this, context);
    }
    
    public <V> V deserialize(final DataObject data, final Class<V> type, final Context context) {
        if (!type.isAssignableFrom(this.getType())) {
            throw new RuntimeException("Invalid type: " + type);
        }
        final T t = this.deserialize(data, context);
        if (type.isInstance(t)) {
            return type.cast(t);
        }
        throw new RuntimeException("Invalid type: " + type + " for instance: " + t.getClass());
    }
    
    public Map<String, DefaultData> getDefaults() {
        return this.defaults;
    }
    
    private DataValue getValue(final String key, final DataObject holder) {
        final DataValue value = holder.get(key);
        if (value.isNonNull()) {
            return value;
        }
        return this.getDefault(key);
    }
    
    private DataValue getDefault(final String name) {
        final DefaultData data = this.defaults.get(name);
        if (data.hasValue()) {
            return data.getValue();
        }
        return DataValue.NULL;
    }
    
    public static <T> Builder<T> builder(final Class<T> type, final DataFactory<T> constructor) {
        return builder(type.getSimpleName(), type, constructor);
    }
    
    public static <T> Builder<T> builder(final String name, final Class<T> type, final DataFactory<T> constructor) {
        return new Builder<T>(name, type, constructor);
    }
    
    public static class Builder<T>
    {
        private final String name;
        private final Class<T> type;
        private final DataFactory<T> constructor;
        private final Map<String, DefaultData> defaults;
        private final Map<String, DataAccessor<T, ?>> accessors;
        
        public Builder(final String name, final Class<T> type, final DataFactory<T> constructor) {
            this.defaults = new LinkedHashMap<String, DefaultData>();
            this.accessors = new LinkedHashMap<String, DataAccessor<T, ?>>();
            this.name = name;
            this.type = type;
            this.constructor = constructor;
        }
        
        public <V> Builder<T> add(final String key, final Object value, final Function<T, V> accessor) {
            if (value instanceof Enum) {
                return (Builder<T>)this.add(key, ((Enum)value).name(), t -> ((Enum)accessor.apply(t)).name());
            }
            return this.add(key, value, (DataAccessor<T, Object>)DataAccessor.wrap((Function<T, V>)accessor));
        }
        
        public <V> Builder<T> add(final String key, final Object value, final DataAccessor<T, V> accessor) {
            this.accessors.put(key, accessor);
            this.defaults.put(key, new DefaultData(DataValue.lazy(value)));
            return this;
        }
        
        public <V> Builder<T> add(final String key, final DataValue value, final Function<T, V> accessor) {
            return this.add(key, value, (DataAccessor<T, Object>)DataAccessor.wrap((Function<T, V>)accessor));
        }
        
        public <V> Builder<T> adds(final String key, final Class<V> superType, final Function<T, V> accessor) {
            this.accessors.put(key, DataAccessor.wrap((Function<T, ?>)accessor));
            return this;
        }
        
        public <V> Builder<T> add(final String key, final DataValue value, final DataAccessor<T, V> accessor) {
            this.accessors.put(key, accessor);
            this.defaults.put(key, new DefaultData(value));
            return this;
        }
        
        public <V> Builder<T> addObj(final String key, final Function<T, V> accessor) {
            return this.addObj(key, (DataAccessor<T, Object>)DataAccessor.wrap((Function<T, V>)accessor));
        }
        
        public <V> Builder<T> addObj(final String key, final DataAccessor<T, V> accessor) {
            this.accessors.put(key, accessor);
            this.defaults.put(key, new DefaultData(DataObject.NULL_OBJ));
            return this;
        }
        
        public <V> Builder<T> addObj(final String key, final Class<V> type, final Function<T, ? extends V> accessor) {
            return this.addObj(key, type, (DataAccessor<T, ? extends V>)DataAccessor.wrap((Function<T, ? extends V>)accessor));
        }
        
        public <V> Builder<T> addObj(final String key, final Class<V> type, final DataAccessor<T, ? extends V> accessor) {
            this.accessors.put(key, accessor);
            this.defaults.put(key, new DefaultData(type, DataObject.NULL_OBJ));
            return this;
        }
        
        public <V> Builder<T> addList(final String key, final Function<T, List<V>> accessor) {
            return this.addList(key, (DataAccessor<T, List<Object>>)DataAccessor.wrap((Function<T, List<V>>)accessor));
        }
        
        public <V> Builder<T> addList(final String key, final DataAccessor<T, List<V>> accessor) {
            this.accessors.put(key, accessor);
            this.defaults.put(key, new DefaultData(DataList.NULL_LIST));
            return this;
        }
        
        public <V> Builder<T> addList(final String key, final Class<V> type, final Function<T, List<? extends V>> accessor) {
            return this.addList(key, type, (DataAccessor<T, List<? extends V>>)DataAccessor.wrap((Function<T, List<? extends V>>)accessor));
        }
        
        public <V> Builder<T> addList(final String key, final Class<V> type, final DataAccessor<T, List<? extends V>> accessor) {
            this.accessors.put(key, accessor);
            this.defaults.put(key, new DefaultData(type, DataList.NULL_LIST));
            return this;
        }
        
        public DataSpec<T> build() {
            Objects.requireNonNull(this.constructor, "constructor");
            return new DataSpec<T>(this);
        }
    }
}
