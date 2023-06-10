//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.biome.map.BiomeMap;

public class CoastModifier implements BiomeModifier {
    private final float seaLevel;
    private final BiomeMap<?> biomeMap;

    public CoastModifier(GeneratorContext context, BiomeMap<?> biomeMap) {
        this.seaLevel = context.levels.water;
        this.biomeMap = biomeMap;
    }

    public int priority() {
        return 10;
    }

    public boolean test(int biome, Cell cell) {
        return cell.terrain.isCoast() || cell.terrain.isShallowOcean() && cell.value > this.seaLevel;
    }

    public int modify(int in, Cell cell, int x, int z) {
        int coast = this.biomeMap.getCoast(cell);
        return BiomeMap.isValid(coast) ? coast : in;
    }
}
