// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.serializer.cereal;

import com.terraforged.cereal.value.DataList;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.engine.serialization.serializer.AbstractWriter;

public class CerealWriter extends AbstractWriter<DataValue, DataObject, DataList, CerealWriter>
{
    @Override
    protected CerealWriter self() {
        return this;
    }
    
    @Override
    protected boolean isObject(final DataValue value) {
        return value.isObj();
    }
    
    @Override
    protected boolean isArray(final DataValue value) {
        return value.isList();
    }
    
    @Override
    protected void add(final DataObject parent, final String key, final DataValue value) {
        parent.add(key, value);
    }
    
    @Override
    protected void add(final DataList parent, final DataValue value) {
        parent.add(value);
    }
    
    @Override
    protected DataObject createObject() {
        return new DataObject();
    }
    
    @Override
    protected DataList createArray() {
        return new DataList();
    }
    
    @Override
    protected DataValue closeObject(final DataObject o) {
        return o;
    }
    
    @Override
    protected DataValue closeArray(final DataList a) {
        return a;
    }
    
    @Override
    protected DataValue create(final String value) {
        return DataValue.of(value);
    }
    
    @Override
    protected DataValue create(final int value) {
        return DataValue.of(value);
    }
    
    @Override
    protected DataValue create(final float value) {
        return DataValue.of(value);
    }
    
    @Override
    protected DataValue create(final boolean value) {
        return DataValue.of(value);
    }
}
