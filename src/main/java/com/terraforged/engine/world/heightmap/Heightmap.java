// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.heightmap;

import com.terraforged.engine.Seed;
import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.module.Blender;
import com.terraforged.engine.settings.Settings;
import com.terraforged.engine.settings.TerrainSettings;
import com.terraforged.engine.settings.WorldSettings;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.climate.Climate;
import com.terraforged.engine.world.continent.Continent;
import com.terraforged.engine.world.continent.ContinentLerper2;
import com.terraforged.engine.world.continent.ContinentLerper3;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.engine.world.terrain.provider.TerrainProvider;
import com.terraforged.engine.world.terrain.region.RegionLerper;
import com.terraforged.engine.world.terrain.region.RegionModule;
import com.terraforged.engine.world.terrain.region.RegionSelector;
import com.terraforged.engine.world.terrain.special.VolcanoPopulator;
import com.terraforged.noise.Source;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.func.Interpolation;

public class Heightmap implements Populator
{
    public static final int MOUNTAIN_SCALE = 1000;
    public static final float DEEP_OCEAN_VALUE = 0.1f;
    public static final float SHALLOW_OCEAN_VALUE = 0.25f;
    public static final float BEACH_VALUE = 0.327f;
    public static final float COAST_VALUE = 0.448f;
    public static final float INLAND_VALUE = 0.502f;
    private static final int REGION_SEED_OFFSET = 789124;
    private static final int WARP_SEED_OFFSET = 8934;
    protected final Continent continentGenerator;
    protected final Populator regionModule;
    private final Levels levels;
    private final Climate climate;
    private final Populator root;
    private final TerrainProvider terrainProvider;
    private final float terrainFrequency;
    
    public Heightmap(final GeneratorContext context) {
        final Settings settings = context.settings;
        final WorldSettings world = context.settings.world;
        final ControlPoints controlPoints = new ControlPoints(world.controlPoints);
        final Seed regionSeed = context.seed.offset(789124);
        final Seed regionWarp = context.seed.offset(8934);
        final int regionWarpScale = 400;
        final int regionWarpStrength = 200;
        final RegionConfig regionConfig = new RegionConfig(regionSeed.get(), context.settings.terrain.general.terrainRegionSize, Source.simplex(regionWarp.next(), regionWarpScale, 1), Source.simplex(regionWarp.next(), regionWarpScale, 1), regionWarpStrength);
        this.levels = context.levels;
        this.terrainFrequency = 1.0f / settings.terrain.general.globalHorizontalScale;
        this.regionModule = new RegionModule(regionConfig);
        final Seed mountainSeed = context.seed.offset(context.settings.terrain.general.terrainSeedOffset);
        final Module mountainShapeBase = Source.cellEdge(mountainSeed.next(), 1000, EdgeFunc.DISTANCE_2_ADD).warp(mountainSeed.next(), 333, 2, 250.0);
        final Module mountainShape = mountainShapeBase.curve(Interpolation.CURVE3).clamp(0.0, 0.9).map(0.0, 1.0);
        this.terrainProvider = context.terrainFactory.create(context, regionConfig, this);
        final Populator terrainRegions = new RegionSelector(this.terrainProvider.getPopulators());
        final Populator terrainRegionBorders = TerrainPopulator.of(TerrainType.FLATS, this.terrainProvider.getLandforms().getLandBase(), this.terrainProvider.getLandforms().plains(context.seed), settings.terrain.steppe);
        final Populator terrain = new RegionLerper(terrainRegionBorders, terrainRegions);
        final Populator mountains = this.register(TerrainType.MOUNTAIN_CHAIN, this.terrainProvider.getLandforms().getLandBase(), this.terrainProvider.getLandforms().mountains(mountainSeed), settings.terrain.mountains);
        this.continentGenerator = world.continent.continentType.create(context.seed, context);
        this.climate = new Climate(this.continentGenerator, context);
        final Populator land = new Blender(mountainShape, terrain, mountains, 0.3f, 0.8f, 0.575f);
        final ContinentLerper3 oceans = new ContinentLerper3(this.register(TerrainType.DEEP_OCEAN, this.terrainProvider.getLandforms().deepOcean(context.seed.next())), this.register(TerrainType.SHALLOW_OCEAN, Source.constant(context.levels.water(-7))), this.register(TerrainType.COAST, Source.constant(context.levels.water)), controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast);
        this.root = new ContinentLerper2(oceans, land, controlPoints.shallowOcean, controlPoints.inland);
    }
    
    public TerrainProvider getTerrainProvider() {
        return this.terrainProvider;
    }
    
    public Populator getRegionModule() {
        return this.regionModule;
    }
    
    public Levels getLevels() {
        return this.levels;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float z) {
        this.applyBase(cell, x, z);
        this.applyRivers(cell, x, z);
        this.applyClimate(cell, x, z);
    }
    
    public void applyBase(final Cell cell, float x, float z) {
        cell.terrain = TerrainType.FLATS;
        this.continentGenerator.apply(cell, x, z);
        this.regionModule.apply(cell, x, z);
        x *= this.terrainFrequency;
        z *= this.terrainFrequency;
        this.root.apply(cell, x, z);
    }
    
    public void applyRivers(final Cell cell, final float x, final float z, final Rivermap rivermap) {
        rivermap.apply(cell, x, z);
        VolcanoPopulator.modifyVolcanoType(cell, this.levels);
    }
    
    public void applyClimate(final Cell cell, final float x, final float z) {
        this.climate.apply(cell, x, z);
    }
    
    public Climate getClimate() {
        return this.climate;
    }
    
    public Continent getContinent() {
        return this.continentGenerator;
    }
    
    public Rivermap getRivermap(final int x, final int z) {
        return this.continentGenerator.getRivermap(x, z);
    }
    
    public Populator getPopulator(final Terrain terrain, final int id) {
        return this.terrainProvider.getPopulator(terrain, id);
    }
    
    private void applyRivers(final Cell cell, final float x, final float z) {
        this.applyRivers(cell, x, z, this.continentGenerator.getRivermap(cell));
    }
    
    private TerrainPopulator register(final Terrain terrain, final Module variance) {
        final TerrainPopulator populator = TerrainPopulator.of(terrain, variance);
        this.terrainProvider.registerMixable(populator);
        return populator;
    }
    
    private TerrainPopulator register(final Terrain terrain, final Module base, final Module variance, final TerrainSettings.Terrain settings) {
        final TerrainPopulator populator = TerrainPopulator.of(terrain, base, variance, settings);
        this.terrainProvider.registerMixable(populator);
        return populator;
    }
}
