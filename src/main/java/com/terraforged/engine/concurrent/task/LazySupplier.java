// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.task;

import java.util.function.Function;
import java.util.function.Supplier;

public class LazySupplier<T> extends LazyCallable<T>
{
    private final Supplier<T> supplier;
    
    public LazySupplier(final Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    protected T create() {
        return this.supplier.get();
    }
    
    public <V> LazySupplier<V> then(final Function<T, V> mapper) {
        return supplied(this, mapper);
    }
    
    public static <T> LazySupplier<T> of(final Supplier<T> supplier) {
        return new LazySupplier<T>(supplier);
    }
    
    public static <V, T> LazySupplier<T> factory(final V value, final Function<V, T> function) {
        return of(() -> function.apply(value));
    }
    
    public static <V, T> LazySupplier<T> supplied(final Supplier<V> supplier, final Function<V, T> function) {
        return of(() -> function.apply(supplier.get()));
    }
}
