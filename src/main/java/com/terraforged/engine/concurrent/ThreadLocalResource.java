//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.concurrent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThreadLocalResource<T> extends ThreadLocal<Resource<T>> {
    private final Supplier<Resource<T>> supplier;

    private ThreadLocalResource(Supplier<Resource<T>> supplier) {
        this.supplier = supplier;
    }

    public T open() {
        return (T) ((Resource)this.get()).get();
    }

    public void close() {
        ((Resource)this.get()).close();
    }

    protected Resource<T> initialValue() {
        return (Resource)this.supplier.get();
    }

    public static <T> ThreadLocalResource<T> withInitial(Supplier<T> supplier, Consumer<T> consumer) {
        return new ThreadLocalResource(() -> {
            return new SimpleResource(supplier.get(), consumer);
        });
    }
}
