//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.BiomeContext;
import com.terraforged.engine.world.biome.type.BiomeType;

public class WetlandModifier implements BiomeModifier {
    private final int wetland;
    private final int coldWetland;
    private final int frozenWetland;

    public <T> WetlandModifier(BiomeContext<T> context, T normal, T cold, T frozen) {
        this.wetland = context.getId(normal);
        this.coldWetland = context.getId(cold);
        this.frozenWetland = context.getId(frozen);
    }

    public int priority() {
        return 0;
    }

    public boolean test(int biome, Cell cell) {
        if (cell.biome != BiomeType.TAIGA) {
            if (cell.biome == BiomeType.TUNDRA) {
                return biome == this.coldWetland;
            } else {
                return false;
            }
        } else {
            return biome == this.wetland || biome == this.frozenWetland;
        }
    }

    public int modify(int in, Cell cell, int x, int z) {
        return cell.biome == BiomeType.TAIGA ? this.coldWetland : this.frozenWetland;
    }
}
