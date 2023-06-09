// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent;

import com.terraforged.engine.concurrent.cache.SafeCloseable;

public interface Resource<T> extends SafeCloseable
{
    public static final Resource NONE = new Resource() {
        @Override
        public Object get() {
            return null;
        }
        
        @Override
        public boolean isOpen() {
            return false;
        }
        
        @Override
        public void close() {
        }
    };
    
    T get();
    
    boolean isOpen();
    
    default <T> Resource<T> empty() {
        return (Resource<T>)Resource.NONE;
    }
}
