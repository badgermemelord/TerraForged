// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.value;

import com.terraforged.cereal.serial.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DataList extends DataValue implements Iterable<DataValue>
{
    public static final DataList NULL_LIST;
    private final boolean nullable;
    private final List<DataValue> data;
    
    protected DataList(final List<DataValue> data, final boolean nullable) {
        super(data);
        this.data = data;
        this.nullable = nullable;
    }
    
    public DataList() {
        this(false);
    }
    
    public DataList(final int size) {
        this(size, false);
    }
    
    public DataList(final boolean nullable) {
        this(16, nullable);
    }
    
    public DataList(final int size, final boolean nullable) {
        this(new ArrayList<DataValue>(size), nullable);
    }
    
    public int size() {
        return this.data.size();
    }
    
    public boolean contains(final Object value) {
        for (final DataValue v : this) {
            if (value.equals(v.value)) {
                return true;
            }
        }
        return false;
    }
    
    public DataValue get(final int index) {
        if (index < this.data.size()) {
            final DataValue value = this.data.get(index);
            if (value != null) {
                return value;
            }
        }
        return DataValue.NULL;
    }
    
    public DataObject getObj(final int index) {
        return this.get(index).asObj();
    }
    
    public DataList getList(final int index) {
        return this.get(index).asList();
    }
    
    public DataList add(final Object value) {
        return this.add(DataValue.of(value));
    }
    
    public DataList add(final DataValue value) {
        if (value.isNonNull() || this.nullable) {
            this.data.add(value);
        }
        return this;
    }
    
    public DataValue set(final int index, final Object value) {
        return this.set(index, DataValue.of(value));
    }
    
    public DataValue set(final int index, final DataValue value) {
        if (value.isNonNull() || this.nullable) {
            final DataValue removed = this.data.set(index, value);
            if (removed != null) {
                return removed;
            }
        }
        return DataValue.NULL;
    }
    
    public DataValue remove(final int index) {
        if (index < this.size()) {
            final DataValue value = this.data.remove(index);
            if (value != null) {
                return value;
            }
        }
        return DataValue.NULL;
    }
    
    public List<DataValue> getBacking() {
        return this.data;
    }
    
    @Override
    public void appendTo(final DataWriter writer) throws IOException {
        writer.beginList();
        for (final DataValue value : this.data) {
            writer.value(value);
        }
        writer.endList();
    }
    
    @Override
    public Iterator<DataValue> iterator() {
        return this.data.iterator();
    }
    
    static {
        NULL_LIST = new DataList(Collections.emptyList(), false);
    }
}
