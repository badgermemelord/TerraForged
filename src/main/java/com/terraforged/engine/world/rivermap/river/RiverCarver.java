// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.river;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.noise.Source;
import com.terraforged.noise.func.CurveFunc;
import com.terraforged.noise.func.SCurve;
import com.terraforged.noise.source.Line;
import com.terraforged.noise.util.NoiseUtil;

import java.util.Random;

public class RiverCarver extends TerrainPopulator implements Comparable<RiverCarver>
{
    public final boolean main;
    private final boolean connecting;
    private final float fade;
    private final float fadeInv;
    private final Range bedWidth;
    private final Range banksWidth;
    private final Range valleyWidth;
    private final Range bedDepth;
    private final Range banksDepth;
    private final float waterLine;
    public final River river;
    public final RiverWarp warp;
    public final RiverConfig config;
    public final CurveFunc valleyCurve;
    
    public RiverCarver(final River river, final RiverWarp warp, final RiverConfig config, final Settings settings, final Levels levels) {
        super(TerrainType.RIVER, Source.ZERO, Source.ZERO, 1.0f);
        this.fade = settings.fadeIn;
        this.fadeInv = 1.0f / settings.fadeIn;
        this.bedWidth = new Range(0.25f, (float)(config.bedWidth * config.bedWidth));
        this.banksWidth = new Range(1.5625f, (float)(config.bankWidth * config.bankWidth));
        this.valleyWidth = new Range(settings.valleySize * settings.valleySize, settings.valleySize * settings.valleySize);
        this.river = river;
        this.warp = warp;
        this.config = config;
        this.main = config.main;
        this.connecting = settings.connecting;
        this.waterLine = levels.water;
        this.bedDepth = new Range(levels.water, config.bedHeight);
        this.banksDepth = new Range(config.minBankHeight, config.maxBankHeight);
        this.valleyCurve = settings.valleyCurve;
    }
    
    @Override
    public void apply(final Cell cell, final float x, final float z) {
    }
    
    @Override
    public int compareTo(final RiverCarver o) {
        return Integer.compare(this.config.order, o.config.order);
    }
    
    public void carve(final Cell cell, final float px, final float pz, final float pt, final float x, final float z, final float t) {
        final float d2 = this.getDistance2(x, z, t);
        final float pd2 = this.getDistance2(px, pz, pt);
        float valleyAlpha = this.getDistanceAlpha(pt, Math.min(d2, pd2), this.valleyWidth);
        if (valleyAlpha == 0.0f) {
            return;
        }
        final float bankHeight = this.getScaledSize(t, this.banksDepth);
        valleyAlpha = this.valleyCurve.apply(valleyAlpha);
        cell.riverMask = Math.min(cell.riverMask, 1.0f - valleyAlpha);
        cell.value = Math.min(NoiseUtil.lerp(cell.value, bankHeight, valleyAlpha), cell.value);
        if (!this.connecting || t > 1.0f) {}
        final float mouthModifier = getMouthModifier(cell);
        final float bedHeight = this.getScaledSize(t, this.bedDepth);
        final float banksAlpha = this.getDistanceAlpha(t, d2 * mouthModifier, this.banksWidth);
        if (banksAlpha == 0.0f) {
            return;
        }
        if (cell.value > bedHeight) {
            cell.value = Math.min(NoiseUtil.lerp(cell.value, bedHeight, banksAlpha), cell.value);
            this.tag(cell, bedHeight);
        }
        final float bedAlpha = this.getDistanceAlpha(t, d2, this.bedWidth);
        if (bedAlpha != 0.0f && cell.value > bedHeight) {
            cell.value = NoiseUtil.lerp(cell.value, bedHeight, bedAlpha);
            this.tag(cell, bedHeight);
        }
    }
    
    public RiverConfig createForkConfig(final float t, final Levels levels) {
        final int bedHeight = levels.scale(this.getScaledSize(t, this.bedDepth));
        int bedWidth = (int)Math.round(Math.sqrt(this.getScaledSize(t, this.bedWidth)) * 0.75);
        int bankWidth = (int)Math.round(Math.sqrt(this.getScaledSize(t, this.banksWidth)) * 0.75);
        bedWidth = Math.max(1, bedWidth);
        bankWidth = Math.max(bedWidth + 1, bankWidth);
        return this.config.createFork(bedHeight, bedWidth, bankWidth, levels);
    }
    
    private float getDistance2(final float x, final float y, final float t) {
        if (t <= 0.0f) {
            return Line.dist2(x, y, this.river.x1, this.river.z1);
        }
        if (t >= 1.0f) {
            return Line.dist2(x, y, this.river.x2, this.river.z2);
        }
        final float px = this.river.x1 + t * this.river.dx;
        final float py = this.river.z1 + t * this.river.dz;
        return Line.dist2(x, y, px, py);
    }
    
    private float getDistanceAlpha(final float t, final float dist2, final Range range) {
        final float size2 = this.getScaledSize(t, range);
        if (dist2 >= size2) {
            return 0.0f;
        }
        return 1.0f - dist2 / size2;
    }
    
    private float getScaledSize(final float t, final Range range) {
        if (t < 0.0f) {
            return range.min;
        }
        if (t > 1.0f) {
            return range.max;
        }
        if (range.min == range.max) {
            return range.min;
        }
        if (t >= this.fade) {
            return range.max;
        }
        return NoiseUtil.lerp(range.min, range.max, t * this.fadeInv);
    }
    
    private void tag(final Cell cell, final float bedHeight) {
        if (cell.terrain.overridesRiver() && (cell.value < bedHeight || cell.value > this.waterLine)) {
            return;
        }
        cell.erosionMask = true;
        if (cell.value <= this.waterLine) {
            cell.terrain = TerrainType.RIVER;
        }
    }
    
    private static float getMouthModifier(final Cell cell) {
        float modifier = NoiseUtil.map(cell.continentEdge, 0.0f, 0.5f, 0.5f);
        modifier *= modifier;
        return modifier;
    }
    
    public static CurveFunc getValleyType(final Random random) {
        final int value = random.nextInt(100);
        if (value < 5) {
            return new SCurve(0.4f, 1.0f);
        }
        if (value < 30) {
            return new SCurve(4.0f, 5.0f);
        }
        if (value < 50) {
            return new SCurve(3.0f, 0.25f);
        }
        return new SCurve(2.0f, -0.5f);
    }
    
    public static RiverCarver create(final float x1, final float z1, final float x2, final float z2, final RiverConfig config, final Levels levels, final Random random) {
        final River river = new River(x1, z1, x2, z2);
        final RiverWarp warp = RiverWarp.create(0.35f, random);
        final float valleyWidth = 275.0f * River.MAIN_VALLEY.next(random);
        final Settings settings = creatSettings(random);
        settings.connecting = false;
        settings.fadeIn = config.fade;
        settings.valleySize = valleyWidth;
        return new RiverCarver(river, warp, config, settings, levels);
    }
    
    private static Settings creatSettings(final Random random) {
        final Settings settings = new Settings();
        settings.valleyCurve = getValleyType(random);
        return settings;
    }
    
    public static class Settings
    {
        public float valleySize;
        public float fadeIn;
        public boolean connecting;
        public CurveFunc valleyCurve;
        
        public Settings() {
            this.valleySize = 275.0f;
            this.fadeIn = 0.7f;
            this.connecting = false;
            this.valleyCurve = new SCurve(2.0f, -0.5f);
        }
    }
}
