// 
// Decompiled by Procyon v0.5.36
// 
package com.terraforged.cereal.spec;

import java.util.function.Function;

public interface DataAccessor<T, V> {
    V access(T var1, Context var2);

    static <T, V> DataAccessor<T, V> wrap(Function<T, V> func) {
        return (owner, context) -> {
            return func.apply(owner);
        };
    }
}