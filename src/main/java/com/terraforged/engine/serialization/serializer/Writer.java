// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.serializer;

public interface Writer
{
    Writer name(final String p0);
    
    Writer beginObject();
    
    Writer endObject();
    
    Writer beginArray();
    
    Writer endArray();
    
    Writer value(final String p0);
    
    Writer value(final float p0);
    
    Writer value(final int p0);
    
    Writer value(final boolean p0);
    
    default void readFrom(final Object value) throws IllegalAccessException {
        new Serializer();
        Serializer.serialize(value, this);
    }
}
