// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.spec;

import com.terraforged.cereal.value.DataValue;

import java.util.List;
import java.util.function.Supplier;

public class DefaultData
{
    private final Class<?> type;
    private final Supplier<DataValue> supplier;
    
    private DefaultData(final Class<?> type, final Supplier<DataValue> supplier) {
        this.type = type;
        this.supplier = supplier;
    }
    
    public DefaultData(final Class<?> type, final DataValue value) {
        this(type, () -> value);
    }
    
    public DefaultData(final DataValue value) {
        this(() -> value);
    }
    
    public DefaultData(final Supplier<DataValue> supplier) {
        this(Object.class, supplier);
    }
    
    public boolean hasSpec() {
        return this.type != Object.class;
    }
    
    public boolean hasValue() {
        return this.type == Object.class;
    }
    
    public List<DataSpec<?>> getSpecs() {
        return DataSpecs.getSpecs(this.type);
    }
    
    public DataValue getValue() {
        return this.supplier.get();
    }
}
