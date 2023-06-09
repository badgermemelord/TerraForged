// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.spec;

import java.util.function.Function;

public interface DataAccessor<T, V>
{
    V access(final T p0, final Context p1);
    
    default <T, V> DataAccessor<T, V> wrap(final Function<T, V> func) {
        return (DataAccessor<T, V>)((owner, context) -> func.apply(owner));
    }
}
