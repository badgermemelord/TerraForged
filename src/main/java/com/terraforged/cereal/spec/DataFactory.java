// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.spec;

import com.terraforged.cereal.value.DataObject;

public interface DataFactory<T>
{
    T create(final DataObject p0, final DataSpec<T> p1, final Context p2);
}
