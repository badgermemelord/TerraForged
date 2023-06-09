// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent;

import java.util.function.Consumer;

public class SimpleResource<T> implements Resource<T>
{
    private final T value;
    private final Consumer<T> closer;
    private boolean open;
    
    public SimpleResource(final T value, final Consumer<T> closer) {
        this.open = false;
        this.value = value;
        this.closer = closer;
    }
    
    @Override
    public T get() {
        this.open = true;
        return this.value;
    }
    
    @Override
    public boolean isOpen() {
        return this.open;
    }
    
    @Override
    public void close() {
        if (this.open) {
            this.open = false;
            this.closer.accept(this.value);
        }
    }
}
