//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.world.biome.TempCategory;
import com.terraforged.engine.world.biome.map.BiomeMap.Builder;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiomes;
import com.terraforged.engine.world.biome.map.defaults.FallbackBiomes;
import com.terraforged.engine.world.biome.type.BiomeType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BiomeMapBuilder<T> implements Builder<T> {
    protected final Map<TempCategory, IntList> rivers = new HashMap();
    protected final Map<TempCategory, IntList> lakes = new HashMap();
    protected final Map<TempCategory, IntList> coasts = new HashMap();
    protected final Map<TempCategory, IntList> beaches = new HashMap();
    protected final Map<TempCategory, IntList> oceans = new HashMap();
    protected final Map<TempCategory, IntList> deepOceans = new HashMap();
    protected final Map<TempCategory, IntList> mountains = new HashMap();
    protected final Map<TempCategory, IntList> volcanoes = new HashMap();
    protected final Map<TempCategory, IntList> wetlands = new HashMap();
    protected final Map<BiomeType, IntList> map = new EnumMap(BiomeType.class);
    protected final BiomeContext<T> context;
    protected final DefaultBiomes defaults;
    protected final FallbackBiomes<T> fallbacks;
    private final Function<BiomeMapBuilder<T>, BiomeMap<T>> constructor;

    BiomeMapBuilder(BiomeContext<T> context, Function<BiomeMapBuilder<T>, BiomeMap<T>> constructor) {
        this.context = context;
        this.constructor = constructor;
        this.defaults = context.getDefaults().getDefaults();
        this.fallbacks = context.getDefaults().getFallbacks();
    }

    public Builder<T> addOcean(T biome, int count) {
        TempCategory category = this.context.getProperties().getTempCategory(biome);
        if (this.context.getProperties().getDepth(biome) < -1.0F) {
            this.add((IntList)this.deepOceans.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        } else {
            this.add((IntList)this.oceans.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        }

        return this;
    }

    public Builder<T> addBeach(T biome, int count) {
        TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add((IntList)this.beaches.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }

    public Builder<T> addCoast(T biome, int count) {
        TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add((IntList)this.coasts.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }

    public Builder<T> addRiver(T biome, int count) {
        TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add((IntList)this.rivers.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }

    public Builder<T> addLake(T biome, int count) {
        TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add((IntList)this.lakes.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }

    public Builder<T> addWetland(T biome, int count) {
        TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add((IntList)this.wetlands.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }

    public Builder<T> addMountain(T biome, int count) {
        TempCategory category = this.context.getProperties().getMountainCategory(biome);
        this.add((IntList)this.mountains.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }

    public Builder<T> addVolcano(T biome, int count) {
        TempCategory category = this.context.getProperties().getTempCategory(biome);
        this.add((IntList)this.volcanoes.computeIfAbsent(category, c -> new IntArrayList()), biome, count);
        return this;
    }

    public Builder<T> addLand(BiomeType type, T biome, int count) {
        this.add((IntList)this.map.computeIfAbsent(type, t -> new IntArrayList()), biome, count);
        return this;
    }

    public BiomeMap<T> build() {
        this.makeSafe();
        return (BiomeMap<T>)this.constructor.apply(this);
    }

    private void makeSafe() {
        this.addIfEmpty(this.rivers, (T)this.fallbacks.river, TempCategory.class);
        this.addIfEmpty(this.lakes, (T)this.fallbacks.lake, TempCategory.class);
        this.addIfEmpty(this.beaches, (T)this.fallbacks.beach, TempCategory.class);
        this.addIfEmpty(this.oceans, (T)this.fallbacks.ocean, TempCategory.class);
        this.addIfEmpty(this.deepOceans, (T)this.fallbacks.deepOcean, TempCategory.class);
        this.addIfEmpty(this.wetlands, (T)this.fallbacks.wetland, TempCategory.class);
        this.addIfEmpty(this.map, (T)this.fallbacks.land, BiomeType.class);
    }

    private void add(IntList list, T biome, int count) {
        if (biome != null) {
            int id = this.context.getId(biome);

            for(int i = 0; i < count; ++i) {
                list.add(id);
            }
        }
    }

    private <E extends Enum<E>> void addIfEmpty(Map<E, IntList> map, T biome, Class<E> enumType) {
        for(E e : enumType.getEnumConstants()) {
            this.addIfEmpty((IntList)map.computeIfAbsent(e, t -> new IntArrayList()), biome);
        }
    }

    private void addIfEmpty(IntList list, T biome) {
        if (list.isEmpty()) {
            list.add(this.context.getId(biome));
        }
    }

    public static <T> Builder<T> create(BiomeContext<T> context) {
        return new BiomeMapBuilder<>(context, SimpleBiomeMap::new);
    }
}
