// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadLocalResource<T> extends ThreadLocal<Resource<T>>
{
    private final Supplier<Resource<T>> supplier;
    
    private ThreadLocalResource(final Supplier<Resource<T>> supplier) {
        this.supplier = supplier;
    }
    
    public T open() {
        return this.get().get();
    }
    
    public void close() {
        this.get().close();
    }
    
    @Override
    protected Resource<T> initialValue() {
        return this.supplier.get();
    }
    
    public static <T> ThreadLocalResource<T> withInitial(final Supplier<T> supplier, final Consumer<T> consumer) {
        return new ThreadLocalResource<T>(() -> new SimpleResource(supplier.get(), (Consumer<Object>)consumer));
    }
}
