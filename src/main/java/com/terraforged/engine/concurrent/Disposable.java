// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent;

public interface Disposable
{
    void dispose();
    
    public interface Listener<T>
    {
        void onDispose(final T p0);
    }
}
