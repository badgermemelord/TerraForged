// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.provider;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.settings.TerrainSettings;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.heightmap.RegionConfig;
import com.terraforged.engine.world.terrain.LandForms;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.engine.world.terrain.special.VolcanoPopulator;
import com.terraforged.noise.Source;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class StandardTerrainProvider implements TerrainProvider
{
    private final List<TerrainPopulator> mixable;
    private final List<TerrainPopulator> unmixable;
    private final Map<Terrain, List<Populator>> populators;
    private final Seed seed;
    private final Levels levels;
    private final LandForms landForms;
    private final RegionConfig config;
    private final TerrainSettings settings;
    private final Populator defaultPopulator;
    
    public StandardTerrainProvider(final GeneratorContext context, final RegionConfig config, final Populator defaultPopulator) {
        this.mixable = new ArrayList<TerrainPopulator>();
        this.unmixable = new ArrayList<TerrainPopulator>();
        this.populators = new HashMap<Terrain, List<Populator>>();
        this.seed = context.seed.offset(context.settings.terrain.general.terrainSeedOffset);
        this.config = config;
        this.levels = context.levels;
        this.settings = context.settings.terrain;
        this.landForms = new LandForms(context.settings.terrain, context.levels, this.createGroundNoise(context));
        this.defaultPopulator = defaultPopulator;
        this.init(context);
    }
    
    protected Module createGroundNoise(final GeneratorContext context) {
        return Source.constant(context.levels.ground);
    }
    
    protected void init(final GeneratorContext context) {
        this.registerMixable(TerrainType.FLATS, this.landForms.getLandBase(), this.landForms.steppe(this.seed), this.settings.steppe);
        this.registerMixable(TerrainType.FLATS, this.landForms.getLandBase(), this.landForms.plains(this.seed), this.settings.plains);
        this.registerMixable(TerrainType.HILLS, this.landForms.getLandBase(), this.landForms.dales(this.seed), this.settings.dales);
        this.registerMixable(TerrainType.HILLS, this.landForms.getLandBase(), this.landForms.hills1(this.seed), this.settings.hills);
        this.registerMixable(TerrainType.HILLS, this.landForms.getLandBase(), this.landForms.hills2(this.seed), this.settings.hills);
        this.registerMixable(TerrainType.HILLS, this.landForms.getLandBase(), this.landForms.torridonian(this.seed), this.settings.torridonian);
        this.registerMixable(TerrainType.PLATEAU, this.landForms.getLandBase(), this.landForms.plateau(this.seed), this.settings.plateau);
        this.registerMixable(TerrainType.BADLANDS, this.landForms.getLandBase(), this.landForms.badlands(this.seed), this.settings.badlands);
        this.registerUnMixable(TerrainType.BADLANDS, this.landForms.getLandBase(), this.landForms.badlands(this.seed), this.settings.badlands);
        this.registerUnMixable(TerrainType.MOUNTAINS, this.landForms.getLandBase(), this.landForms.mountains(this.seed), this.settings.mountains);
        this.registerUnMixable(TerrainType.MOUNTAINS, this.landForms.getLandBase(), this.landForms.mountains2(this.seed), this.settings.mountains);
        this.registerUnMixable(TerrainType.MOUNTAINS, this.landForms.getLandBase(), this.landForms.mountains3(this.seed), this.settings.mountains);
        this.registerUnMixable(new VolcanoPopulator(this.seed, this.config, this.levels, this.settings.volcano.weight));
    }
    
    @Override
    public void forEach(final Consumer<TerrainPopulator> consumer) {
        this.mixable.forEach(consumer);
        this.unmixable.forEach(consumer);
    }
    
    @Override
    public Terrain getTerrain(final String name) {
        for (final Terrain terrain : this.populators.keySet()) {
            if (terrain.getName().equalsIgnoreCase(name)) {
                return terrain;
            }
        }
        return null;
    }
    
    @Override
    public void registerMixable(final TerrainPopulator populator) {
        this.populators.computeIfAbsent(populator.getType(), t -> new ArrayList()).add(populator);
        this.mixable.add(populator);
    }
    
    @Override
    public void registerUnMixable(final TerrainPopulator populator) {
        this.populators.computeIfAbsent(populator.getType(), t -> new ArrayList()).add(populator);
        this.unmixable.add(populator);
    }
    
    @Override
    public int getVariantCount(final Terrain terrain) {
        final List<Populator> list = this.populators.get(terrain);
        if (list == null) {
            return 0;
        }
        return list.size();
    }
    
    @Override
    public Populator getPopulator(final Terrain terrain, int variant) {
        if (variant < 0) {
            return this.defaultPopulator;
        }
        final List<Populator> list = this.populators.get(terrain);
        if (list == null) {
            return this.defaultPopulator;
        }
        if (variant >= list.size()) {
            variant = list.size() - 1;
        }
        return list.get(variant);
    }
    
    @Override
    public LandForms getLandforms() {
        return this.landForms;
    }
    
    @Override
    public List<Populator> getPopulators() {
        final List<TerrainPopulator> mixed = combine(getMixable(this.mixable), this::combine);
        final List<Populator> result = new ArrayList<Populator>(mixed.size() + this.unmixable.size());
        result.addAll(mixed);
        result.addAll(this.unmixable);
        Collections.shuffle(result, new Random(this.seed.next()));
        return result;
    }
    
    public List<TerrainPopulator> getTerrainPopulators() {
        final List<TerrainPopulator> populators = new ArrayList<TerrainPopulator>();
        populators.addAll(this.mixable);
        populators.addAll(this.unmixable);
        return populators;
    }
    
    private TerrainPopulator combine(final TerrainPopulator tp1, final TerrainPopulator tp2) {
        return this.combine(tp1, tp2, this.seed, this.config.scale / 2);
    }
    
    private TerrainPopulator combine(final TerrainPopulator tp1, final TerrainPopulator tp2, final Seed seed, final int scale) {
        final Terrain type = TerrainType.registerComposite(tp1.getType(), tp2.getType());
        final Module combined = Source.perlin(seed.next(), scale, 1).warp(seed.next(), scale / 2, 2, scale / 2.0).blend(tp1.getVariance(), tp2.getVariance(), 0.5, 0.25).clamp(0.0, 1.0);
        final float weight = (tp1.getWeight() + tp2.getWeight()) / 2.0f;
        return new TerrainPopulator(type, this.landForms.getLandBase(), combined, weight);
    }
    
    private static <T> List<T> combine(final List<T> input, final BiFunction<T, T, T> operator) {
        int length = input.size();
        for (int i = 1; i < input.size(); ++i) {
            length += input.size() - i;
        }
        final List<T> result = new ArrayList<T>(length);
        for (int j = 0; j < length; ++j) {
            result.add(null);
        }
        int j = 0;
        int k = input.size();
        while (j < input.size()) {
            final T t1 = input.get(j);
            result.set(j, t1);
            for (int l = j + 1; l < input.size(); ++l, ++k) {
                final T t2 = input.get(l);
                final T t3 = operator.apply(t1, t2);
                result.set(k, t3);
            }
            ++j;
        }
        return result;
    }
    
    private static List<TerrainPopulator> getMixable(final List<TerrainPopulator> input) {
        final List<TerrainPopulator> output = new ArrayList<TerrainPopulator>(input.size());
        for (final TerrainPopulator populator : input) {
            if (populator.getWeight() > 0.0f) {
                output.add(populator);
            }
        }
        return output;
    }
}
