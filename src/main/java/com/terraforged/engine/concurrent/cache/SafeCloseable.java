// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.cache;

public interface SafeCloseable extends AutoCloseable
{
    public static final SafeCloseable NONE = () -> {};
    
    void close();
}
