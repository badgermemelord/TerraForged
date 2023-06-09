// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.world.biome.TempCategory;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiomes;
import com.terraforged.engine.world.biome.map.defaults.FallbackBiomes;
import com.terraforged.engine.world.biome.type.BiomeType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BiomeMapBuilder<T> implements BiomeMap.Builder<T>
{
    protected final Map<TempCategory, IntList> rivers;
    protected final Map<TempCategory, IntList> lakes;
    protected final Map<TempCategory, IntList> coasts;
    protected final Map<TempCategory, IntList> beaches;
    protected final Map<TempCategory, IntList> oceans;
    protected final Map<TempCategory, IntList> deepOceans;
    protected final Map<TempCategory, IntList> mountains;
    protected final Map<TempCategory, IntList> volcanoes;
    protected final Map<TempCategory, IntList> wetlands;
    protected final Map<BiomeType, IntList> map;
    protected final BiomeContext<T> context;
    protected final DefaultBiomes defaults;
    protected final FallbackBiomes<T> fallbacks;
    private final Function<BiomeMapBuilder<T>, BiomeMap<T>> constructor;
    
    BiomeMapBuilder(final BiomeContext<T> context, final Function<BiomeMapBuilder<T>, BiomeMap<T>> constructor) {
        this.rivers = new HashMap<TempCategory, IntList>();
        this.lakes = new HashMap<TempCategory, IntList>();
        this.coasts = new HashMap<TempCategory, IntList>();
        this.beaches = new HashMap<TempCategory, IntList>();
        this.oceans = new HashMap<TempCategory, IntList>();
        this.deepOceans = new HashMap<TempCategory, IntList>();
        this.mountains = new HashMap<TempCategory, IntList>();
        this.volcanoes = new HashMap<TempCategory, IntList>();
        this.wetlands = new HashMap<TempCategory, IntList>();
        this.map = new EnumMap<BiomeType, IntList>(BiomeType.class);
        this.context = context;
        this.constructor = constructor;
        this.defaults = context.getDefaults().getDefaults();
        this.fallbacks = context.getDefaults().getFallbacks();
    }
    
    @Override
    public BiomeMap.Builder<T> addOcean(final T biome, final int count) {
        final TempCategory category = this.context.getProperties().getTempCategory(biome);
        if (this.context.getProperties().getDepth(biome) < -1.0f) {
            this.add(this.deepOceans.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        }
        else {
            this.add(this.oceans.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        }
        return this;
    }
    
    @Override
    public BiomeMap.Builder<T> addBeach(final T biome, final int count) {
        final TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add(this.beaches.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }
    
    @Override
    public BiomeMap.Builder<T> addCoast(final T biome, final int count) {
        final TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add(this.coasts.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }
    
    @Override
    public BiomeMap.Builder<T> addRiver(final T biome, final int count) {
        final TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add(this.rivers.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }
    
    @Override
    public BiomeMap.Builder<T> addLake(final T biome, final int count) {
        final TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add(this.lakes.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }
    
    @Override
    public BiomeMap.Builder<T> addWetland(final T biome, final int count) {
        final TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add(this.wetlands.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }
    
    @Override
    public BiomeMap.Builder<T> addMountain(final T biome, final int count) {
        final TempCategory category = this.context.getProperties().getMountainCategory(biome);
        this.add(this.mountains.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }
    
    @Override
    public BiomeMap.Builder<T> addVolcano(final T biome, final int count) {
        final TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add(this.volcanoes.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }
    
    @Override
    public BiomeMap.Builder<T> addLand(final BiomeType type, final T biome, final int count) {
        this.add(this.map.computeIfAbsent(type, t -> new IntArrayList()), biome, count);
        return this;
    }
    
    @Override
    public BiomeMap<T> build() {
        this.makeSafe();
        return this.constructor.apply(this);
    }
    
    private void makeSafe() {
        this.addIfEmpty(this.rivers, this.fallbacks.river, TempCategory.class);
        this.addIfEmpty(this.lakes, this.fallbacks.lake, TempCategory.class);
        this.addIfEmpty(this.beaches, this.fallbacks.beach, TempCategory.class);
        this.addIfEmpty(this.oceans, this.fallbacks.ocean, TempCategory.class);
        this.addIfEmpty(this.deepOceans, this.fallbacks.deepOcean, TempCategory.class);
        this.addIfEmpty(this.wetlands, this.fallbacks.wetland, TempCategory.class);
        this.addIfEmpty(this.map, this.fallbacks.land, BiomeType.class);
    }
    
    private void add(final IntList list, final T biome, final int count) {
        if (biome != null) {
            final int id = this.context.getId(biome);
            for (int i = 0; i < count; ++i) {
                list.add(id);
            }
        }
    }
    
    private <E extends Enum<E>> void addIfEmpty(final Map<E, IntList> map, final T biome, final Class<E> enumType) {
        for (final E e : enumType.getEnumConstants()) {
            this.addIfEmpty(map.computeIfAbsent(e, t -> new IntArrayList()), biome);
        }
    }
    
    private void addIfEmpty(final IntList list, final T biome) {
        if (list.isEmpty()) {
            list.add(this.context.getId(biome));
        }
    }
    
    public static <T> BiomeMap.Builder<T> create(final BiomeContext<T> context) {
        return new BiomeMapBuilder<T>(context, (Function<BiomeMapBuilder<T>, BiomeMap<T>>)SimpleBiomeMap::new);
    }
}
