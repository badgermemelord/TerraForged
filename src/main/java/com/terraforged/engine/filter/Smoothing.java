// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.settings.Settings;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.noise.util.NoiseUtil;

public class Smoothing implements Filter
{
    private final int radius;
    private final float rad2;
    private final float strength;
    private final Modifier modifier;
    
    public Smoothing(final Settings settings, final Levels levels) {
        this.radius = NoiseUtil.round(settings.filters.smoothing.smoothingRadius + 0.5f);
        this.rad2 = settings.filters.smoothing.smoothingRadius * settings.filters.smoothing.smoothingRadius;
        this.strength = settings.filters.smoothing.smoothingRate;
        this.modifier = Modifier.range(levels.ground(1), levels.ground(120)).invert();
    }
    
    @Override
    public void apply(final Filterable map, final int seedX, final int seedZ, int iterations) {
        while (iterations-- > 0) {
            this.apply(map);
        }
    }
    
    private void apply(final Filterable cellMap) {
        final int maxZ = cellMap.getSize().total - this.radius;
        final int maxX = cellMap.getSize().total - this.radius;
        for (int z = this.radius; z < maxZ; ++z) {
            for (int x = this.radius; x < maxX; ++x) {
                final Cell cell = cellMap.getCellRaw(x, z);
                if (!cell.erosionMask) {
                    float total = 0.0f;
                    float weights = 0.0f;
                    for (int dz = -this.radius; dz <= this.radius; ++dz) {
                        for (int dx = -this.radius; dx <= this.radius; ++dx) {
                            final float dist2 = (float)(dx * dx + dz * dz);
                            if (dist2 <= this.rad2) {
                                final int px = x + dx;
                                final int pz = z + dz;
                                final Cell neighbour = cellMap.getCellRaw(px, pz);
                                if (!neighbour.isAbsent()) {
                                    final float value = neighbour.value;
                                    final float weight = 1.0f - dist2 / this.rad2;
                                    total += value * weight;
                                    weights += weight;
                                }
                            }
                        }
                    }
                    if (weights > 0.0f) {
                        final float dif = cell.value - total / weights;
                        final Cell cell2 = cell;
                        cell2.value -= this.modifier.modify(cell, dif * this.strength);
                    }
                }
            }
        }
    }
}
