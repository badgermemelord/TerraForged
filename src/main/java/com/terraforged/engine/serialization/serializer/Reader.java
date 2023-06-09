// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.serializer;

import java.util.Collection;

public interface Reader
{
    int getSize();
    
    Reader getChild(final String p0);
    
    Reader getChild(final int p0);
    
    Collection<String> getKeys();
    
    String getString();
    
    boolean getBool();
    
    float getFloat();
    
    int getInt();
    
    default boolean has(final String key) {
        return this.getKeys().contains(key);
    }
    
    default String getString(final String key) {
        return this.getChild(key).getString();
    }
    
    default boolean getBool(final String key) {
        return this.getChild(key).getBool();
    }
    
    default float getFloat(final String key) {
        return this.getChild(key).getFloat();
    }
    
    default int getInt(final String key) {
        return this.getChild(key).getInt();
    }
    
    default String getString(final int index) {
        return this.getChild(index).getString();
    }
    
    default boolean getBool(final int index) {
        return this.getChild(index).getBool();
    }
    
    default float getFloat(final int index) {
        return this.getChild(index).getFloat();
    }
    
    default int getInt(final int index) {
        return this.getChild(index).getInt();
    }
    
    default boolean writeTo(final Object object) throws Throwable {
        return Deserializer.deserialize(this, object);
    }
}
