// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world;

import java.util.concurrent.locks.StampedLock;
import java.util.function.IntFunction;

public class WorldErosion<T>
{
    private volatile T value;
    private final IntFunction<T> factory;
    private final Validator<T> validator;
    private final StampedLock lock;
    
    public WorldErosion(final IntFunction<T> factory, final Validator<T> validator) {
        this.value = null;
        this.lock = new StampedLock();
        this.factory = factory;
        this.validator = validator;
    }
    
    public T get(final int ctx) {
        final T value = this.readValue();
        if (this.validate(value, ctx)) {
            return value;
        }
        return this.writeValue(ctx);
    }
    
    private T readValue() {
        final long optRead = this.lock.tryOptimisticRead();
        final T value = this.value;
        if (!this.lock.validate(optRead)) {
            final long stamp = this.lock.readLock();
            try {
                return this.value;
            }
            finally {
                this.lock.unlockRead(stamp);
            }
        }
        return value;
    }
    
    private T writeValue(final int ctx) {
        final long stamp = this.lock.writeLock();
        try {
            if (this.validate(this.value, ctx)) {
                return this.value;
            }
            return this.value = this.factory.apply(ctx);
        }
        finally {
            this.lock.unlockWrite(stamp);
        }
    }
    
    private boolean validate(final T value, final int ctx) {
        return value != null && this.validator.validate(value, ctx);
    }
    
    public interface Validator<T>
    {
        boolean validate(final T p0, final int p1);
    }
}
