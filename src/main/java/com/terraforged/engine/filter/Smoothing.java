//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.settings.Settings;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.noise.util.NoiseUtil;

public class Smoothing implements Filter {
    private final int radius;
    private final float rad2;
    private final float strength;
    private final Modifier modifier;

    public Smoothing(Settings settings, Levels levels) {
        this.radius = NoiseUtil.round(settings.filters.smoothing.smoothingRadius + 0.5F);
        this.rad2 = settings.filters.smoothing.smoothingRadius * settings.filters.smoothing.smoothingRadius;
        this.strength = settings.filters.smoothing.smoothingRate;
        this.modifier = Modifier.range(levels.ground(1), levels.ground(120)).invert();
    }

    public void apply(Filterable map, int seedX, int seedZ, int iterations) {
        while(iterations-- > 0) {
            this.apply(map);
        }
    }

    private void apply(Filterable cellMap) {
        int maxZ = cellMap.getSize().total - this.radius;
        int maxX = cellMap.getSize().total - this.radius;

        for(int z = this.radius; z < maxZ; ++z) {
            for(int x = this.radius; x < maxX; ++x) {
                Cell cell = cellMap.getCellRaw(x, z);
                if (!cell.erosionMask) {
                    float total = 0.0F;
                    float weights = 0.0F;

                    for(int dz = -this.radius; dz <= this.radius; ++dz) {
                        for(int dx = -this.radius; dx <= this.radius; ++dx) {
                            float dist2 = (float)(dx * dx + dz * dz);
                            if (!(dist2 > this.rad2)) {
                                int px = x + dx;
                                int pz = z + dz;
                                Cell neighbour = cellMap.getCellRaw(px, pz);
                                if (!neighbour.isAbsent()) {
                                    float value = neighbour.value;
                                    float weight = 1.0F - dist2 / this.rad2;
                                    total += value * weight;
                                    weights += weight;
                                }
                            }
                        }
                    }

                    if (weights > 0.0F) {
                        float dif = cell.value - total / weights;
                        cell.value -= this.modifier.modify(cell, dif * this.strength);
                    }
                }
            }
        }
    }
}
