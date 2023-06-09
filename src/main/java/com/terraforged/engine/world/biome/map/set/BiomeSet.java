// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map.set;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiome;
import com.terraforged.noise.util.NoiseUtil;
import it.unimi.dsi.fastutil.ints.*;

import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class BiomeSet
{
    private static final int[] EMPTY;
    private static final IntList EMPTY_LIST;
    protected final int[][] biomes;
    protected final DefaultBiome defaultBiome;
    
    public BiomeSet(final int[][] biomes, final DefaultBiome defaultBiome) {
        this.biomes = biomes;
        this.defaultBiome = defaultBiome;
    }
    
    public int getSize(final int index) {
        return this.biomes[index].length;
    }
    
    public int getSize(final Cell cell) {
        return this.biomes[this.getIndex(cell)].length;
    }
    
    public int[] getSet(final int index) {
        return this.biomes[index];
    }
    
    public int[] getSet(final Cell cell) {
        return this.biomes[this.getIndex(cell)];
    }
    
    public int getBiome(final Cell cell) {
        final int[] set = this.biomes[this.getIndex(cell)];
        if (set.length == 0) {
            return this.defaultBiome.getDefaultBiome(cell);
        }
        final int maxIndex = set.length - 1;
        final int index = NoiseUtil.round(maxIndex * cell.biomeRegionId);
        if (index < 0 || index >= set.length) {
            return this.defaultBiome.getDefaultBiome(cell);
        }
        return set[index];
    }
    
    public abstract int getIndex(final Cell p0);
    
    public abstract void forEach(final BiConsumer<String, int[]> p0);
    
    protected static int[][] collect(final Map<? extends Enum<?>, IntList> map, final int size, final Function<Enum<?>, Integer> indexer, final IntComparator comparator) {
        final int[][] biomes = new int[size][];
        for (final Enum<?> type : map.keySet()) {
            final int index = indexer.apply(type);
            if (index >= 0) {
                if (index >= size) {
                    continue;
                }
                IntList list = map.getOrDefault(type, BiomeSet.EMPTY_LIST);
                list = minimize(list);
                list.sort((Comparator)comparator);
                biomes[index] = list.toIntArray();
            }
        }
        for (int i = 0; i < size; ++i) {
            if (biomes[i] == null) {
                biomes[i] = BiomeSet.EMPTY;
            }
        }
        return biomes;
    }
    
    private static IntList minimize(final IntList list) {
        final Int2IntMap counts = count(list);
        final IntList result = (IntList)new IntArrayList(list.size());
        final int min = counts.values().stream().min(Integer::compareTo).orElse(1);
        for (final int t : list) {
            final int count = counts.get(t);
            for (int amount = count / min, i = 0; i < amount; ++i) {
                result.add(t);
            }
        }
        return result;
    }
    
    private static Int2IntMap count(final IntList list) {
        final Int2IntMap map = (Int2IntMap)new Int2IntOpenHashMap();
        for (final int t : list) {
            int count = map.getOrDefault(t, 0);
            map.put(t, ++count);
        }
        return map;
    }
    
    static {
        EMPTY = new int[0];
        EMPTY_LIST = (IntList)new IntArrayList();
    }
}
