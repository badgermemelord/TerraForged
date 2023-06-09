// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain;

import com.terraforged.engine.Seed;
import com.terraforged.engine.module.Ridge;
import com.terraforged.engine.settings.TerrainSettings;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.noise.Source;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.func.Interpolation;

public class LandForms
{
    private static final int PLAINS_H = 250;
    private static final int MOUNTAINS_H = 410;
    private static final double MOUNTAINS_V = 0.7;
    private static final int MOUNTAINS2_H = 400;
    private static final double MOUNTAINS2_V = 0.645;
    private final TerrainSettings settings;
    private final float terrainVerticalScale;
    private final float seaLevel;
    private final Module ground;
    
    public LandForms(final TerrainSettings settings, final Levels levels, final Module ground) {
        this.settings = settings;
        this.ground = ground;
        this.terrainVerticalScale = settings.general.globalVerticalScale;
        this.seaLevel = levels.water;
    }
    
    public Module getOceanBase() {
        return Source.ZERO;
    }
    
    public Module getLandBase() {
        return this.ground;
    }
    
    public Module deepOcean(int seed) {
        final Module hills = Source.perlin(++seed, 150, 3).scale(this.seaLevel * 0.7).bias(Source.perlin(++seed, 200, 1).scale(this.seaLevel * 0.2f));
        final Module canyons = Source.perlin(++seed, 150, 4).powCurve(0.2).invert().scale(this.seaLevel * 0.7).bias(Source.perlin(++seed, 170, 1).scale(this.seaLevel * 0.15f));
        return Source.perlin(++seed, 500, 1).blend(hills, canyons, 0.6, 0.65).warp(++seed, 50, 2, 50.0);
    }
    
    public Module steppe(final Seed seed) {
        final int scaleH = Math.round(250.0f * this.settings.steppe.horizontalScale);
        final double erosionAmount = 0.45;
        final Module erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        final Module warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        final Module warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        final Module module = Source.perlin(seed.next(), scaleH, 1).mult(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 200.0);
        return module.scale(0.08).bias(-0.02);
    }
    
    public Module plains(final Seed seed) {
        final int scaleH = Math.round(250.0f * this.settings.plains.horizontalScale);
        final double erosionAmount = 0.45;
        final Module erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        final Module warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        final Module warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        final Module module = Source.perlin(seed.next(), scaleH, 1).mult(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 256.0);
        return module.scale(0.15f * this.terrainVerticalScale).bias(-0.02);
    }
    
    public Module plateau(final Seed seed) {
        final Module valley = Source.ridge(seed.next(), 500, 1).invert().warp(seed.next(), 100, 1, 150.0).warp(seed.next(), 20, 1, 15.0);
        final Module top = Source.build(seed.next(), 150, 3).lacunarity(2.45).ridge().warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 40, 2, 20.0).scale(0.15).mult(valley.clamp(0.02, 0.1).map(0.0, 1.0));
        final Module surface = Source.perlin(seed.next(), 20, 3).scale(0.05).warp(seed.next(), 40, 2, 20.0);
        final Module module = valley.mult(Source.cubic(seed.next(), 500, 1).scale(0.6).bias(0.3)).add(top).terrace(Source.constant(0.9), Source.constant(0.15), Source.constant(0.35), 4, 0.4).add(surface);
        return module.scale(0.475 * this.terrainVerticalScale);
    }
    
    public Module hills1(final Seed seed) {
        return Source.perlin(seed.next(), 200, 3).mult(Source.billow(seed.next(), 400, 3).alpha(0.5)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).scale(0.6f * this.terrainVerticalScale);
    }
    
    public Module hills2(final Seed seed) {
        return Source.cubic(seed.next(), 128, 2).mult(Source.perlin(seed.next(), 32, 4).alpha(0.075)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).mult(Source.ridge(seed.next(), 512, 2).alpha(0.8)).scale(0.55f * this.terrainVerticalScale);
    }
    
    public Module dales(final Seed seed) {
        final Module hills1 = Source.build(seed.next(), 300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75);
        final Module hills2 = Source.build(seed.next(), 350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25);
        final Module combined = Source.perlin(seed.next(), 400, 1).clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
        final Module hills3 = combined.pow(1.125).warp(seed.next(), 300, 1, 100.0);
        return hills3.scale(0.4);
    }
    
    public Module mountains(final Seed seed) {
        final int scaleH = Math.round(410.0f * this.settings.mountains.horizontalScale);
        final Module module = Source.build(seed.next(), scaleH, 4).gain(1.15).lacunarity(2.35).ridge().mult(Source.perlin(seed.next(), 24, 4).alpha(0.075)).warp(seed.next(), 350, 1, 150.0);
        return this.makeFancy(seed, module).scale(0.7 * this.terrainVerticalScale);
    }
    
    public Module mountains2(final Seed seed) {
        final Module cell = Source.cellEdge(seed.next(), 360, EdgeFunc.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        final Module blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        final Module surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        final Module mountains = cell.clamp(0.0, 1.0).mult(blur).mult(surface).pow(1.1);
        return this.makeFancy(seed, mountains).scale(0.645 * this.terrainVerticalScale);
    }
    
    public Module mountains3(final Seed seed) {
        final Module cell = Source.cellEdge(seed.next(), 400, EdgeFunc.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        final Module blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        final Module surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        final Module mountains = cell.clamp(0.0, 1.0).mult(blur).mult(surface).pow(1.1);
        final Module terraced = mountains.terrace(Source.perlin(seed.next(), 50, 1).scale(0.5), Source.perlin(seed.next(), 100, 1).clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.20000000298023224, 0.44999998807907104, 24, 1);
        return this.makeFancy(seed, terraced).scale(0.645 * this.terrainVerticalScale);
    }
    
    public Module badlands(final Seed seed) {
        final Module mask = Source.build(seed.next(), 270, 3).perlin().clamp(0.35, 0.65).map(0.0, 1.0);
        final Module hills = Source.ridge(seed.next(), 275, 4).warp(seed.next(), 400, 2, 100.0).warp(seed.next(), 18, 1, 20.0).mult(mask);
        final double modulation = 0.4;
        final double alpha = 1.0 - modulation;
        final Module mod1 = hills.warp(seed.next(), 100, 1, 50.0).scale(modulation);
        final Module lowFreq = hills.steps(4, 0.6, 0.7).scale(alpha).add(mod1);
        final Module highFreq = hills.steps(10, 0.6, 0.7).scale(alpha).add(mod1);
        final Module detail = lowFreq.add(highFreq);
        final Module mod2 = hills.mult(Source.perlin(seed.next(), 200, 3).scale(modulation));
        final Module shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
        final Module badlands = shape.mult(detail.alpha(0.5));
        return badlands.scale(0.55).bias(0.025);
    }
    
    public Module torridonian(final Seed seed) {
        final Module plains = Source.perlin(seed.next(), 100, 3).warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 20, 1, 40.0).scale(0.15);
        final Module hills = Source.perlin(seed.next(), 150, 4).warp(seed.next(), 300, 1, 200.0).warp(seed.next(), 20, 2, 20.0).boost();
        final Module module = Source.perlin(seed.next(), 200, 3).blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(seed.next(), 120, 1).scale(0.25), Source.perlin(seed.next(), 200, 1).scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
        return module.scale(0.5);
    }
    
    private Module makeFancy(final Seed seed, final Module module) {
        if (this.settings.general.fancyMountains) {
            final Domain warp = Domain.direction(Source.perlin(seed.next(), 10, 1), Source.constant(2.0));
            final Ridge erosion = new Ridge(seed.next(), 2, 0.65f, 128.0f, 0.15f, 3.1f, 0.8f, Ridge.Mode.CONSTANT);
            return erosion.wrap(module).warp(warp);
        }
        return module;
    }
}
