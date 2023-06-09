// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache;

public interface ExpiringEntry
{
    long getTimestamp();
    
    default void close() {
    }
}
