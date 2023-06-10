//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome;

import com.terraforged.engine.util.ListUtils;
import com.terraforged.engine.world.biome.map.BiomeContext;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class DesertBiomes {
    private final IntSet reds;
    private final IntSet whites;
    private final IntSet deserts;
    private final IntList redSand;
    private final IntList whiteSand;
    private final int maxRedIndex;
    private final int maxWhiteIndex;
    private final int defaultRed;
    private final int defaultWhite;

    public DesertBiomes(IntList deserts, IntList redSand, IntList whiteSand, int defaultRed, int defaultWhite, BiomeContext<?> context) {
        this.deserts = new IntOpenHashSet(deserts);
        this.whites = new IntOpenHashSet(whiteSand);
        this.reds = new IntOpenHashSet(redSand);
        this.redSand = redSand;
        this.whiteSand = whiteSand;
        this.whiteSand.sort(context);
        this.redSand.sort(context);
        this.maxRedIndex = redSand.size() - 1;
        this.maxWhiteIndex = whiteSand.size() - 1;
        this.defaultRed = defaultRed;
        this.defaultWhite = defaultWhite;
    }

    public boolean isDesert(int biome) {
        return this.deserts.contains(biome);
    }

    public boolean isRedDesert(int biome) {
        return this.reds.contains(biome);
    }

    public boolean isWhiteDesert(int biome) {
        return this.whites.contains(biome);
    }

    public int getRedDesert(float shape) {
        return ListUtils.get(this.redSand, this.maxRedIndex, shape, this.defaultRed);
    }

    public int getWhiteDesert(float shape) {
        return ListUtils.get(this.whiteSand, this.maxWhiteIndex, shape, this.defaultWhite);
    }
}
