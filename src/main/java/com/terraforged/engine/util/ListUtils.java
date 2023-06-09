// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util;

import com.terraforged.noise.util.NoiseUtil;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtils
{
    public static <T> T get(final List<T> list, final float value, final T def) {
        if (list.isEmpty()) {
            return def;
        }
        return get(list, list.size() - 1, value, def);
    }
    
    public static int get(final IntList list, final float value, final int def) {
        if (list.isEmpty()) {
            return def;
        }
        return get(list, list.size() - 1, value, def);
    }
    
    public static <T> T get(final List<T> list, final int maxIndex, final float value, final T def) {
        if (maxIndex <= 0 || list.isEmpty()) {
            return def;
        }
        final int index = NoiseUtil.round(value * maxIndex);
        if (index < list.size()) {
            return list.get(index);
        }
        return def;
    }
    
    public static int get(final IntList list, final int maxIndex, final float value, final int def) {
        if (maxIndex <= 0 || list.isEmpty()) {
            return def;
        }
        final int index = NoiseUtil.round(value * maxIndex);
        if (index < list.size()) {
            return list.getInt(index);
        }
        return def;
    }
    
    public static <T> List<T> minimize(final List<T> list) {
        final Map<T, Integer> counts = count(list);
        final List<T> result = new ArrayList<T>(list.size());
        final int min = counts.values().stream().min(Integer::compareTo).orElse(1);
        for (final T t : list) {
            final int count = counts.get(t);
            for (int amount = count / min, i = 0; i < amount; ++i) {
                result.add(t);
            }
        }
        return result;
    }
    
    public static <T> Map<T, Integer> count(final List<T> list) {
        final Map<T, Integer> map = new HashMap<T, Integer>(list.size());
        for (final T t : list) {
            int count = map.getOrDefault(t, 0);
            map.put(t, ++count);
        }
        return map;
    }
    
    public static IntSet combine(final IntList a, final IntList b) {
        final IntSet set = (IntSet)new IntOpenHashSet((IntCollection)a);
        set.addAll((IntCollection)b);
        return set;
    }
}
