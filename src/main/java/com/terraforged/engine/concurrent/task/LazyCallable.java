// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.task;

import com.terraforged.engine.concurrent.thread.ThreadPool;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class LazyCallable<T> implements Callable<T>, Future<T>, Supplier<T>
{
    private final StampedLock lock;
    protected volatile T value;
    
    public LazyCallable() {
        this.lock = new StampedLock();
        this.value = null;
    }
    
    @Override
    public final T call() {
        final long optRead = this.lock.tryOptimisticRead();
        T result = this.value;
        if (this.lock.validate(optRead) && result != null) {
            return result;
        }
        final long read = this.lock.readLock();
        try {
            result = this.value;
            if (result != null) {
                return result;
            }
        }
        finally {
            this.lock.unlockRead(read);
        }
        final long write = this.lock.writeLock();
        try {
            result = this.value;
            if (result == null) {
                result = this.create();
                Objects.requireNonNull(result);
                this.value = result;
            }
            return result;
        }
        finally {
            this.lock.unlockWrite(write);
        }
    }
    
    @Override
    public final boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }
    
    @Override
    public final boolean isCancelled() {
        return false;
    }
    
    @Override
    public boolean isDone() {
        final long optRead = this.lock.tryOptimisticRead();
        final boolean done = this.value != null;
        if (this.lock.validate(optRead)) {
            return done;
        }
        final long read = this.lock.readLock();
        try {
            return this.value != null;
        }
        finally {
            this.lock.unlockRead(read);
        }
    }
    
    @Override
    public T get() {
        return this.call();
    }
    
    @Override
    public T get(final long timeout, final TimeUnit unit) {
        return this.call();
    }
    
    public <V> LazyCallable<V> then(final ThreadPool executor, final Function<T, V> function) {
        return callAsync(() -> function.apply(this.get()), executor);
    }
    
    protected abstract T create();
    
    public static LazyCallable<Void> adapt(final Runnable runnable) {
        return new RunnableAdapter(runnable);
    }
    
    public static <T> LazyCallable<T> adapt(final Callable<T> callable) {
        if (callable instanceof LazyCallable) {
            return (LazyCallable<T>)(LazyCallable)callable;
        }
        return new CallableAdapter<T>(callable);
    }
    
    public static <T> LazyCallable<T> adaptComplete(final Callable<T> callable) {
        return new CompleteAdapter<T>(callable);
    }
    
    public static <T> LazyCallable<T> callAsync(final Callable<T> callable, final ThreadPool executor) {
        return new FutureAdapter<T>(executor.submit(callable));
    }
    
    public static class CallableAdapter<T> extends LazyCallable<T>
    {
        private final Callable<T> callable;
        
        public CallableAdapter(final Callable<T> callable) {
            this.callable = callable;
        }
        
        @Override
        protected T create() {
            try {
                return this.callable.call();
            }
            catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        }
    }
    
    public static class FutureAdapter<T> extends LazyCallable<T>
    {
        private final Future<T> future;
        
        FutureAdapter(final Future<T> future) {
            this.future = future;
        }
        
        @Override
        public boolean isDone() {
            return this.future.isDone();
        }
        
        @Override
        protected T create() {
            try {
                return this.future.get();
            }
            catch (Throwable t) {
                t.printStackTrace();
                return null;
            }
        }
    }
    
    public static class RunnableAdapter extends LazyCallable<Void>
    {
        private final Runnable runnable;
        
        RunnableAdapter(final Runnable runnable) {
            this.runnable = runnable;
        }
        
        @Override
        protected Void create() {
            this.runnable.run();
            return null;
        }
    }
    
    public static class CompleteAdapter<T> extends LazyCallable<T>
    {
        private final Callable<T> callable;
        
        public CompleteAdapter(final Callable<T> callable) {
            this.callable = callable;
        }
        
        @Override
        protected T create() {
            try {
                return this.callable.call();
            }
            catch (Exception e) {
                return null;
            }
        }
        
        @Override
        public boolean isDone() {
            return true;
        }
    }
}
