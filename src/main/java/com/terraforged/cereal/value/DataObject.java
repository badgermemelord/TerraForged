// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.value;

import com.terraforged.cereal.serial.DataWriter;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DataObject extends DataValue implements Iterable<Map.Entry<String, DataValue>>
{
    public static final DataObject NULL_OBJ;
    private final String type;
    private final boolean nullable;
    private final Map<String, DataValue> data;
    
    protected DataObject(final String type, final Map<String, DataValue> data, final boolean nullable) {
        super(data);
        this.type = type;
        this.data = data;
        this.nullable = nullable;
    }
    
    public DataObject() {
        this("");
    }
    
    public DataObject(final String type) {
        this(type, new LinkedHashMap<String, DataValue>(), false);
    }
    
    public String getType() {
        return this.type;
    }
    
    public int size() {
        return this.data.size();
    }
    
    public boolean has(final String key) {
        return this.data.containsKey(key);
    }
    
    public boolean contains(final Object value) {
        for (final DataValue v : this.data.values()) {
            if (value.equals(v.value)) {
                return true;
            }
        }
        return false;
    }
    
    public DataValue get(final String key) {
        return this.data.getOrDefault(key, DataObject.NULL);
    }
    
    public DataObject getObj(final String key) {
        return this.get(key).asObj();
    }
    
    public DataList getList(final String key) {
        return this.get(key).asList();
    }
    
    public DataObject add(final String key, final Object value) {
        return this.add(key, DataValue.of(value));
    }
    
    public DataObject add(final String key, final DataValue value) {
        if (value.isNonNull() || this.nullable) {
            this.data.put(key, value);
        }
        return this;
    }
    
    public DataValue remove(final String key) {
        final DataValue value = this.data.remove(key);
        if (value == null) {
            return DataValue.NULL;
        }
        return value;
    }
    
    public void forEach(final BiConsumer<String, DataValue> consumer) {
        this.data.forEach(consumer);
    }
    
    public Map<String, DataValue> getBacking() {
        return this.data;
    }
    
    @Override
    public void appendTo(final DataWriter writer) throws IOException {
        writer.type(this.type);
        writer.beginObj();
        for (final Map.Entry<String, DataValue> entry : this.data.entrySet()) {
            writer.name(entry.getKey());
            writer.value(entry.getValue());
        }
        writer.endObj();
    }
    
    @Override
    public Iterator<Map.Entry<String, DataValue>> iterator() {
        return this.data.entrySet().iterator();
    }
    
    static {
        NULL_OBJ = new DataObject("null", Collections.emptyMap(), false);
    }
}
