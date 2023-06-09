// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.spec;

import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubSpec<T>
{
    private final Class<T> superType;
    private final Map<Class<? extends T>, DataSpec<? extends T>> children;
    
    public SubSpec(final Class<T> type) {
        this.children = new ConcurrentHashMap<Class<? extends T>, DataSpec<? extends T>>();
        this.superType = type;
    }
    
    public Class<T> getSuperType() {
        return this.superType;
    }
    
    public <V extends T> SubSpec<T> register(final Class<V> type, final DataSpec<V> spec) {
        this.children.put((Class<? extends T>)type, (DataSpec<? extends T>)spec);
        return this;
    }
    
    public T deserialize(final DataObject data, final Context context) {
        for (final DataSpec<? extends T> spec : this.children.values()) {
            if (matches(data, spec)) {
                try {
                    return (T)spec.deserialize(data, context);
                }
                catch (Throwable t) {}
            }
        }
        throw new RuntimeException("Unsupported data: " + data);
    }
    
    public <V extends T> DataValue serialize(final V value, final Context context) {
        final DataSpec<? extends T> spec = this.children.get(value.getClass());
        if (spec == null) {
            throw new RuntimeException("Missing sub-spec for type: " + value.getClass());
        }
        return spec.serialize(value, context);
    }
    
    protected static boolean matches(final DataObject object, final DataSpec<?> spec) {
        for (final Map.Entry<String, DefaultData> entry : spec.getDefaults().entrySet()) {
            if (!object.has(entry.getKey())) {
                return false;
            }
            final DataValue value = object.get(entry.getKey());
            final DataValue defaultValue = entry.getValue().getValue();
            if (!value.matchesType(defaultValue)) {
                return false;
            }
        }
        return true;
    }
}
