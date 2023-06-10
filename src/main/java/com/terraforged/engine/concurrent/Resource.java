//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent;

import com.terraforged.engine.concurrent.cache.SafeCloseable;

public interface Resource<T> extends SafeCloseable {
    Resource NONE = new Resource() {
        @Override
        public Object get() {
            return null;
        }

        @Override
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
