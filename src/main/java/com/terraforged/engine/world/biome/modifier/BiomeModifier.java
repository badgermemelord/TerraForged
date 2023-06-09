// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;

public interface BiomeModifier extends Comparable<BiomeModifier>
{
    int priority();
    
    boolean test(final int p0, final Cell p1);
    
    int modify(final int p0, final Cell p1, final int p2, final int p3);
    
    default boolean exitEarly() {
        return false;
    }
    
    default int compareTo(final BiomeModifier other) {
        return Integer.compare(other.priority(), this.priority());
    }
}
