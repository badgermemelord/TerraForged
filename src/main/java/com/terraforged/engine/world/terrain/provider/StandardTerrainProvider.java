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
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class StandardTerrainProvider implements TerrainProvider {
    private final List<TerrainPopulator> mixable = new ArrayList();
    private final List<TerrainPopulator> unmixable = new ArrayList();
    private final Map<Terrain, List<Populator>> populators = new HashMap();
    private final Seed seed;
    private final Levels levels;
    private final LandForms landForms;
    private final RegionConfig config;
    private final TerrainSettings settings;
    private final Populator defaultPopulator;

    public StandardTerrainProvider(GeneratorContext context, RegionConfig config, Populator defaultPopulator) {
        this.seed = context.seed.offset(context.settings.terrain.general.terrainSeedOffset);
        this.config = config;
        this.levels = context.levels;
        this.settings = context.settings.terrain;
        this.landForms = new LandForms(context.settings.terrain, context.levels, this.createGroundNoise(context));
        this.defaultPopulator = defaultPopulator;
        this.init(context);
    }

    protected Module createGroundNoise(GeneratorContext context) {
        return Source.constant((double)context.levels.ground);
    }

    protected void init(GeneratorContext context) {
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

    public void forEach(Consumer<TerrainPopulator> consumer) {
        this.mixable.forEach(consumer);
        this.unmixable.forEach(consumer);
    }

    public Terrain getTerrain(String name) {
        Iterator var2 = this.populators.keySet().iterator();

        Terrain terrain;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            terrain = (Terrain)var2.next();
        } while(!terrain.getName().equalsIgnoreCase(name));

        return terrain;
    }

    public void registerMixable(TerrainPopulator populator) {
        ((List)this.populators.computeIfAbsent(populator.getType(), (t) -> {
            return new ArrayList();
        })).add(populator);
        this.mixable.add(populator);
    }

    public void registerUnMixable(TerrainPopulator populator) {
        ((List)this.populators.computeIfAbsent(populator.getType(), (t) -> {
            return new ArrayList();
        })).add(populator);
        this.unmixable.add(populator);
    }

    public int getVariantCount(Terrain terrain) {
        List<Populator> list = (List)this.populators.get(terrain);
        return list == null ? 0 : list.size();
    }

    public Populator getPopulator(Terrain terrain, int variant) {
        if (variant < 0) {
            return this.defaultPopulator;
        } else {
            List<Populator> list = (List)this.populators.get(terrain);
            if (list == null) {
                return this.defaultPopulator;
            } else {
                if (variant >= list.size()) {
                    variant = list.size() - 1;
                }

                return (Populator)list.get(variant);
            }
        }
    }

    public LandForms getLandforms() {
        return this.landForms;
    }

    public List<Populator> getPopulators() {
        List<TerrainPopulator> mixed = combine(getMixable(this.mixable), this::combine);
        List<Populator> result = new ArrayList(mixed.size() + this.unmixable.size());
        result.addAll(mixed);
        result.addAll(this.unmixable);
        Collections.shuffle(result, new Random((long)this.seed.next()));
        return result;
    }

    public List<TerrainPopulator> getTerrainPopulators() {
        List<TerrainPopulator> populators = new ArrayList();
        populators.addAll(this.mixable);
        populators.addAll(this.unmixable);
        return populators;
    }

    private TerrainPopulator combine(TerrainPopulator tp1, TerrainPopulator tp2) {
        return this.combine(tp1, tp2, this.seed, this.config.scale / 2);
    }

    private TerrainPopulator combine(TerrainPopulator tp1, TerrainPopulator tp2, Seed seed, int scale) {
        Terrain type = TerrainType.registerComposite(tp1.getType(), tp2.getType());
        Module combined = Source.perlin(seed.next(), scale, 1).warp(seed.next(), scale / 2, 2, (double)scale / 2.0).blend(tp1.getVariance(), tp2.getVariance(), 0.5, 0.25).clamp(0.0, 1.0);
        float weight = (tp1.getWeight() + tp2.getWeight()) / 2.0F;
        return new TerrainPopulator(type, this.landForms.getLandBase(), combined, weight);
    }

    private static <T> List<T> combine(List<T> input, BiFunction<T, T, T> operator) {
        int length = input.size();

        for(int i = 1; i < input.size(); ++i) {
            length += input.size() - i;
        }

        List<T> result = new ArrayList(length);

        int i;
        for(i = 0; i < length; ++i) {
            result.add((T) null);
        }

        i = 0;

        for(int k = input.size(); i < input.size(); ++i) {
            T t1 = input.get(i);
            result.set(i, t1);

            for(int j = i + 1; j < input.size(); ++k) {
                T t2 = input.get(j);
                T t3 = operator.apply(t1, t2);
                result.set(k, t3);
                ++j;
            }
        }

        return result;
    }

    private static List<TerrainPopulator> getMixable(List<TerrainPopulator> input) {
        List<TerrainPopulator> output = new ArrayList(input.size());
        Iterator var2 = input.iterator();

        while(var2.hasNext()) {
            TerrainPopulator populator = (TerrainPopulator)var2.next();
            if (populator.getWeight() > 0.0F) {
                output.add(populator);
            }
        }

        return output;
    }
}
