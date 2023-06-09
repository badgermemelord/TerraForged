// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.region;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.noise.util.NoiseUtil;

import java.util.LinkedList;
import java.util.List;

public class RegionSelector implements Populator
{
    private final int maxIndex;
    private final Populator[] nodes;
    
    public RegionSelector(final List<Populator> populators) {
        this.nodes = getWeightedArray(populators);
        this.maxIndex = this.nodes.length - 1;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float y) {
        this.get(cell.terrainRegionId).apply(cell, x, y);
    }
    
    public Populator get(final float identity) {
        final int index = NoiseUtil.round(identity * this.maxIndex);
        return this.nodes[index];
    }
    
    private static Populator[] getWeightedArray(final List<Populator> modules) {
        float smallest = Float.MAX_VALUE;
        for (final Populator p : modules) {
            if (p instanceof TerrainPopulator) {
                final TerrainPopulator tp = (TerrainPopulator)p;
                if (tp.getWeight() == 0.0f) {
                    continue;
                }
                smallest = Math.min(smallest, tp.getWeight());
            }
            else {
                smallest = Math.min(smallest, 1.0f);
            }
        }
        if (smallest == Float.MAX_VALUE) {
            return modules.toArray(new Populator[0]);
        }
        final List<Populator> result = new LinkedList<Populator>();
        for (final Populator p2 : modules) {
            int count;
            if (p2 instanceof TerrainPopulator) {
                final TerrainPopulator tp2 = (TerrainPopulator)p2;
                if (tp2.getWeight() == 0.0f) {
                    continue;
                }
                count = Math.round(tp2.getWeight() / smallest);
            }
            else {
                count = Math.round(1.0f / smallest);
            }
            while (count-- > 0) {
                result.add(p2);
            }
        }
        if (result.isEmpty()) {
            return modules.toArray(new Populator[0]);
        }
        return result.toArray(new Populator[0]);
    }
}
