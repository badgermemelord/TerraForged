// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.render;

import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.concurrent.thread.ThreadPool;
import com.terraforged.engine.concurrent.thread.ThreadPools;
import com.terraforged.engine.tile.Size;
import com.terraforged.engine.tile.api.TileProvider;
import com.terraforged.engine.tile.gen.TileGenerator;
import com.terraforged.engine.util.RollingGrid;
import com.terraforged.engine.util.pos.PosIterator;

public class RenderWorld2 implements RollingGrid.Generator<RenderWorld2.RegionHolder>
{
    private final int factor;
    private final Size regionSize;
    private final TileProvider cache;
    private final TileGenerator generator;
    private final RenderAPI context;
    private final RegionRenderer renderer;
    private final RollingGrid<RegionHolder> world;
    private final ThreadPool threadPool;
    private boolean first;
    
    public RenderWorld2(final TileGenerator generator, final RenderAPI context, final RenderSettings settings, final int regionCount, final int regionSize) {
        this.threadPool = ThreadPools.createDefault();
        this.first = true;
        this.context = context;
        this.factor = regionSize;
        this.generator = generator;
        this.cache = generator.cached();
        this.regionSize = Size.blocks(regionSize, 0);
        this.renderer = new RegionRenderer(context, settings);
        this.world = new RollingGrid<RegionHolder>(regionCount, RegionHolder[]::new, this);
    }
    
    public boolean isBusy() {
        for (final RegionHolder h : this.world.getIterator()) {
            if (h != null && !h.region.isDone()) {
                return true;
            }
        }
        return false;
    }
    
    public int getResolution() {
        return this.regionSize.total * this.world.getSize();
    }
    
    public int blockToRegion(final int value) {
        return value >> this.factor;
    }
    
    public void init(final int centerX, final int centerZ) {
        this.renderer.getSettings().zoom = 1.0f;
        this.renderer.getSettings().resolution = this.regionSize.total;
        final PosIterator iterator = PosIterator.area(0, 0, this.world.getSize(), this.world.getSize());
        while (iterator.next()) {
            final RegionHolder holder = this.generate(iterator.x(), iterator.z());
            this.world.set(iterator.x(), iterator.z(), holder);
        }
    }
    
    public void move(final int centerX, final int centerZ) {
        if (this.first) {
            this.first = false;
            this.init(centerX, centerZ);
        }
        else {
            this.renderer.getSettings().zoom = 1.0f;
            this.renderer.getSettings().resolution = this.regionSize.total;
            this.world.move(centerX, centerZ);
        }
    }
    
    public void render() {
        final int resolution = this.regionSize.total;
        final float w = this.renderer.getSettings().width * 1.0f / (resolution - 1);
        final float h = this.renderer.getSettings().width * 1.0f / (resolution - 1);
        final float offsetX = this.world.getSize() * this.regionSize.size * w / 2.0f;
        final float offsetZ = this.world.getSize() * this.regionSize.size * h / 2.0f;
        this.context.pushMatrix();
        this.context.translate(-offsetX, -offsetZ, 1000.0f);
        final PosIterator iterator = PosIterator.area(0, 0, this.world.getSize(), this.world.getSize());
        while (iterator.next()) {
            final RegionHolder holder = this.world.get(iterator.x(), iterator.z());
            if (holder != null) {
                if (!holder.region.isDone()) {
                    continue;
                }
                final int relX = iterator.x();
                final int relZ = iterator.z();
                final float startX = relX * this.regionSize.size * w;
                final float startZ = relZ * this.regionSize.size * h;
                final RenderRegion region = holder.region.get();
                this.context.pushMatrix();
                this.context.translate(startX, startZ, 0.0f);
                region.getMesh().draw();
                this.context.popMatrix();
            }
        }
        this.context.popMatrix();
    }
    
    @Override
    public RegionHolder generate(final int x, final int z) {
        return new RegionHolder((LazyCallable)this.generator.getTile(x, z).then(this.threadPool, this.renderer::render));
    }
    
    public static class RegionHolder
    {
        private final LazyCallable<RenderRegion> region;
        
        private RegionHolder(final LazyCallable<RenderRegion> region) {
            this.region = region;
        }
    }
}
