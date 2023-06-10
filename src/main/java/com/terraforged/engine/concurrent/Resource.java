//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.concurrent;

import com.terraforged.engine.concurrent.cache.SafeCloseable;

public interface Resource<T> extends SafeCloseable {
    Resource NONE = new Resource() {
        public Object get() {
            return null;
        }

        public boolean isOpen() {
            return false;
        }

        public void close() {
        }
    };

    T get();

    boolean isOpen();

    static <T> Resource<T> empty() {
        return NONE;
    }
}
