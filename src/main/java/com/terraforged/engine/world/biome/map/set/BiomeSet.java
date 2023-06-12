//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map.set;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiome;
import com.terraforged.noise.util.NoiseUtil;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class BiomeSet {
    private static final int[] EMPTY = new int[0];
    private static final IntList EMPTY_LIST = new IntArrayList();
    protected final int[][] biomes;
    protected final DefaultBiome defaultBiome;

    public BiomeSet(int[][] biomes, DefaultBiome defaultBiome) {
        this.biomes = biomes;
        this.defaultBiome = defaultBiome;
    }

    public int getSize(int index) {
        return this.biomes[index].length;
    }

    public int getSize(Cell cell) {
        return this.biomes[this.getIndex(cell)].length;
    }

    public int[] getSet(int index) {
        return this.biomes[index];
    }

    public int[] getSet(Cell cell) {
        return this.biomes[this.getIndex(cell)];
    }

    public int getBiome(Cell cell) {
        int[] set = this.biomes[this.getIndex(cell)];
        if (set.length == 0) {
            return this.defaultBiome.getDefaultBiome(cell);
        } else {
            int maxIndex = set.length - 1;
            int index = NoiseUtil.round((float)maxIndex * cell.biomeRegionId);
            return index >= 0 && index < set.length ? set[index] : this.defaultBiome.getDefaultBiome(cell);
        }
    }

    public abstract int getIndex(Cell var1);

    public abstract void forEach(BiConsumer<String, int[]> var1);

    protected static int[][] collect(Map<? extends Enum<?>, IntList> map, int size, Function<Enum<?>, Integer> indexer, IntComparator comparator) {
        int[][] biomes = new int[size][];

        for(Enum<?> type : map.keySet()) {
            int index = indexer.apply(type);
            if (index >= 0 && index < size) {
                IntList list = (IntList)map.getOrDefault(type, EMPTY_LIST);
                list = minimize(list);
                list.sort(comparator);
                biomes[index] = list.toIntArray();
            }
        }

        for(int i = 0; i < size; ++i) {
            if (biomes[i] == null) {
                biomes[i] = EMPTY;
            }
        }

        return biomes;
    }

    private static IntList minimize(IntList list) {
        Int2IntMap counts = count(list);
        IntList result = new IntArrayList(list.size());
        int min = counts.values().stream().min(Integer::compareTo).orElse(1);
        IntListIterator var4 = list.iterator();

        while(var4.hasNext()) {
            int t = var4.next();
            int count = counts.get(t);
            int amount = count / min;

            for(int i = 0; i < amount; ++i) {
                result.add(t);
            }
        }

        return result;
    }

    private static Int2IntMap count(IntList list) {
        Int2IntMap map = new Int2IntOpenHashMap();
        IntListIterator var2 = list.iterator();

        while(var2.hasNext()) {
            int t = var2.next();
            int count = map.getOrDefault(t, 0);
            map.put(t, ++count);
        }

        return map;
    }
}
