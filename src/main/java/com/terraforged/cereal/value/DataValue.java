// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.value;

import com.terraforged.cereal.serial.DataWriter;
import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpecs;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DataValue
{
    public static final DataValue NULL;
    protected final Object value;
    
    protected DataValue(final Object value) {
        this.value = value;
    }
    
    public boolean isObj() {
        return this instanceof DataObject;
    }
    
    public boolean isList() {
        return this instanceof DataList;
    }
    
    public boolean isNull() {
        return this == DataValue.NULL;
    }
    
    public boolean isNonNull() {
        return !this.isNull();
    }
    
    public boolean isNum() {
        return this.value instanceof Number;
    }
    
    public boolean isString() {
        return this.value instanceof String;
    }
    
    public boolean isBool() {
        return this.value instanceof Boolean;
    }
    
    public boolean isEnum() {
        return this.value instanceof Enum;
    }
    
    public Number asNum() {
        return (this.value instanceof Number) ? ((Number)this.value) : Integer.valueOf(0);
    }
    
    public DataValue inc(final int amount) {
        return new DataValue(this.asInt() + amount);
    }
    
    public DataValue inc(final double amount) {
        return new DataValue(this.asDouble() + amount);
    }
    
    public byte asByte() {
        return this.asNum().byteValue();
    }
    
    public int asInt() {
        return this.asNum().intValue();
    }
    
    public short aShort() {
        return this.asNum().shortValue();
    }
    
    public long asLong() {
        return this.asNum().longValue();
    }
    
    public float asFloat() {
        return this.asNum().floatValue();
    }
    
    public double asDouble() {
        return this.asNum().doubleValue();
    }
    
    public boolean asBool() {
        if (this.value instanceof Boolean) {
            return (boolean)this.value;
        }
        if (this.value instanceof String) {
            return this.value.toString().equalsIgnoreCase("true");
        }
        return this.asNum().byteValue() == 1;
    }
    
    public String asString() {
        return (this.value == null) ? "null" : this.value.toString();
    }
    
    public <E extends Enum<E>> E asEnum(final Class<E> type) {
        if (type.isInstance(this.value)) {
            return type.cast(this.value);
        }
        if (this.isString()) {
            return Enum.valueOf(type, this.asString());
        }
        if (this.isNum()) {
            final int ordinal = this.asInt();
            final E[] values = type.getEnumConstants();
            if (ordinal < values.length) {
                return values[ordinal];
            }
        }
        throw new IllegalArgumentException("Value is not an Enum");
    }
    
    public DataList asList() {
        return (DataList)((this instanceof DataList) ? this : DataList.NULL_LIST);
    }
    
    public DataObject asObj() {
        return (DataObject)((this instanceof DataObject) ? this : DataObject.NULL_OBJ);
    }
    
    public DataList toList() {
        return (DataList)((this instanceof DataList) ? this : new DataList().add(this));
    }
    
    public <T> T map(final Function<DataValue, T> mapper) {
        return mapper.apply(this);
    }
    
    public <T> Optional<T> map(final Predicate<DataValue> predicate, final Function<DataValue, T> mapper) {
        return Optional.of(this).filter(predicate).map((Function<? super DataValue, ? extends T>)mapper);
    }
    
    public void appendTo(final DataWriter writer) throws IOException {
        writer.value(this.value);
    }
    
    @Override
    public int hashCode() {
        return (this.value == null) ? -1 : this.value.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DataValue value1 = (DataValue)o;
        return Objects.equals(this.value, value1.value);
    }
    
    @Override
    public String toString() {
        return this.asString();
    }
    
    public boolean matchesType(final DataValue other) {
        if (this.getClass() != other.getClass()) {
            return false;
        }
        if (this.value == null) {
            return other.value == null;
        }
        return other.value != null && this.value.getClass() == other.value.getClass();
    }
    
    public static DataValue of(final Object value) {
        return of(value, Context.NONE);
    }
    
    public static DataValue of(final Object value, final Context context) {
        if (value instanceof DataValue) {
            return (DataValue)value;
        }
        if (value instanceof Number) {
            return new DataValue(value);
        }
        if (value instanceof String) {
            return new DataValue(value);
        }
        if (value instanceof Boolean) {
            return new DataValue(value);
        }
        if (value instanceof Enum) {
            return new DataValue(value);
        }
        if (value instanceof List) {
            final List<?> list = (List<?>)value;
            final DataList data = new DataList(list.size());
            for (final Object o : list) {
                data.add(of(o, context));
            }
            return data;
        }
        if (value instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>)value;
            final DataObject data2 = new DataObject();
            for (final Map.Entry<?, ?> e : map.entrySet()) {
                data2.add(e.getKey().toString(), of(e.getValue(), context));
            }
            return data2;
        }
        if (value != null) {
            final String name = value.getClass().getSimpleName();
            if (DataSpecs.hasSpec(name)) {
                return DataSpecs.getSpec(name).serialize(value, context);
            }
        }
        return DataValue.NULL;
    }
    
    public static Supplier<DataValue> lazy(final Object value) {
        return new Supplier<DataValue>() {
            private final Object val = value;
            private DataValue data = null;
            
            @Override
            public DataValue get() {
                if (this.data == null) {
                    this.data = DataValue.of(this.val);
                }
                return this.data;
            }
        };
    }
    
    static {
        NULL = new DataValue(null);
    }
}
