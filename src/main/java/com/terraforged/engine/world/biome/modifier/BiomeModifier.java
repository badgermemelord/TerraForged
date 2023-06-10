//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;

public interface BiomeModifier extends Comparable<BiomeModifier> {
    int priority();

    boolean test(int var1, Cell var2);

    int modify(int var1, Cell var2, int var3, int var4);

    default boolean exitEarly() {
        return false;
    }

    default int compareTo(BiomeModifier other) {
        return Integer.compare(other.priority(), this.priority());
    }
}
