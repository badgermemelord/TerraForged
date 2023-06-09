// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.batch;

public interface BatchTask extends Runnable
{
    public static final Notifier NONE = () -> {};
    
    void setNotifier(final Notifier p0);
    
    public interface Notifier
    {
        void markDone();
    }
}
