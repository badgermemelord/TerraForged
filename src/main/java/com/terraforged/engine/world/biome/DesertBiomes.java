// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome;

import com.terraforged.engine.util.ListUtils;
import com.terraforged.engine.world.biome.map.BiomeContext;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Comparator;

public class DesertBiomes
{
    private final IntSet reds;
    private final IntSet whites;
    private final IntSet deserts;
    private final IntList redSand;
    private final IntList whiteSand;
    private final int maxRedIndex;
    private final int maxWhiteIndex;
    private final int defaultRed;
    private final int defaultWhite;
    
    public DesertBiomes(final IntList deserts, final IntList redSand, final IntList whiteSand, final int defaultRed, final int defaultWhite, final BiomeContext<?> context) {
        this.deserts = (IntSet)new IntOpenHashSet((IntCollection)deserts);
        this.whites = (IntSet)new IntOpenHashSet((IntCollection)whiteSand);
        this.reds = (IntSet)new IntOpenHashSet((IntCollection)redSand);
        this.redSand = redSand;
        (this.whiteSand = whiteSand).sort((Comparator)context);
        this.redSand.sort((Comparator)context);
        this.maxRedIndex = redSand.size() - 1;
        this.maxWhiteIndex = whiteSand.size() - 1;
        this.defaultRed = defaultRed;
        this.defaultWhite = defaultWhite;
    }
    
    public boolean isDesert(final int biome) {
        return this.deserts.contains(biome);
    }
    
    public boolean isRedDesert(final int biome) {
        return this.reds.contains(biome);
    }
    
    public boolean isWhiteDesert(final int biome) {
        return this.whites.contains(biome);
    }
    
    public int getRedDesert(final float shape) {
        return ListUtils.get(this.redSand, this.maxRedIndex, shape, this.defaultRed);
    }
    
    public int getWhiteDesert(final float shape) {
        return ListUtils.get(this.whiteSand, this.maxWhiteIndex, shape, this.defaultWhite);
    }
}
