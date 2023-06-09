// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.render;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.cache.CacheEntry;
import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.concurrent.thread.ThreadPool;
import com.terraforged.engine.tile.Size;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.tile.gen.TileGenerator;
import com.terraforged.engine.world.heightmap.HeightmapCache;

public class RenderWorld
{
    private final int regionCount;
    private final Size regionSize;
    private final RenderAPI context;
    private final ThreadPool threadPool;
    private final RegionRenderer renderer;
    private final TileGenerator generator;
    private final RenderRegion[] view;
    private final LazyCallable<RenderRegion>[] queue;
    private final HeightmapCache heightmapCache;
    
    public RenderWorld(final ThreadPool threadPool, final TileGenerator generator, final RenderAPI context, final RenderSettings settings, final int regionCount, final int regionSize) {
        this.threadPool = threadPool;
        this.context = context;
        this.generator = generator;
        this.regionCount = regionCount;
        this.renderer = new RegionRenderer(context, settings);
        this.regionSize = Size.blocks(regionSize, 0);
        this.queue = (LazyCallable<RenderRegion>[])new LazyCallable[regionCount * regionCount];
        this.view = new RenderRegion[regionCount * regionCount];
        this.heightmapCache = new HeightmapCache(generator.getGenerator().getHeightmap());
    }
    
    public int getResolution() {
        return this.regionSize.total;
    }
    
    public int getSize() {
        return this.regionSize.total * this.regionCount;
    }
    
    public boolean isRendering() {
        for (final LazyCallable<?> entry : this.queue) {
            if (entry != null) {
                return true;
            }
        }
        return false;
    }
    
    public Cell getCenter() {
        final float cx = this.regionCount / 2.0f;
        final float cz = this.regionCount / 2.0f;
        final int rx = (int)cx;
        final int rz = (int)cz;
        final int index = rx + this.regionCount * rz;
        final RenderRegion renderRegion = this.view[index];
        if (renderRegion == null) {
            return Cell.empty();
        }
        final float ox = cx - rx;
        final float oz = cz - rz;
        final Tile tile = renderRegion.getTile();
        final int dx = (int)(tile.getBlockSize().size * ox);
        final int dz = (int)(tile.getBlockSize().size * oz);
        return tile.getCell(dx, dz);
    }
    
    public void redraw() {
        for (final RenderRegion region : this.view) {
            if (region != null) {
                this.renderer.render(region);
            }
        }
    }
    
    public void refresh() {
        for (final LazyCallable<?> entry : this.queue) {
            if (entry != null && !entry.isDone()) {
                return;
            }
        }
        for (int i = 0; i < this.queue.length; ++i) {
            final LazyCallable<RenderRegion> entry2 = this.queue[i];
            if (entry2 != null) {
                if (entry2.isDone()) {
                    this.queue[i] = null;
                    this.view[i] = entry2.get();
                }
            }
        }
    }
    
    public void update(final float x, final float y, final float zoom, final boolean filters) {
        this.renderer.getSettings().zoom = zoom;
        this.renderer.getSettings().resolution = this.getResolution();
        final float factor = (this.regionCount > 1) ? ((this.regionCount - 1.0f) / this.regionCount) : 0.0f;
        final float offset = this.regionSize.size * zoom * factor;
        for (int rz = 0; rz < this.regionCount; ++rz) {
            for (int rx = 0; rx < this.regionCount; ++rx) {
                final int index = rx + rz * this.regionCount;
                final float px = x + rx * this.regionSize.size * zoom - offset;
                final float py = y + rz * this.regionSize.size * zoom - offset;
                this.queue[index] = this.generator.getTile(px, py, zoom, filters).then(this.threadPool, this.renderer::render);
            }
        }
    }
    
    public void render() {
        final int resolution = this.getResolution();
        final float w = this.renderer.getSettings().width / (float)(resolution - 1);
        final float h = this.renderer.getSettings().width / (float)(resolution - 1);
        final float offsetX = this.regionSize.size * this.regionCount * w / 2.0f;
        final float offsetY = this.regionSize.size * this.regionCount * w / 2.0f;
        this.context.pushMatrix();
        this.context.translate(-offsetX, -offsetY, 0.0f);
        for (int rz = 0; rz < this.regionCount; ++rz) {
            for (int rx = 0; rx < this.regionCount; ++rx) {
                final int index = rx + rz * this.regionCount;
                final RenderRegion region = this.view[index];
                if (region != null) {
                    this.context.pushMatrix();
                    final float x = rx * this.regionSize.size * w;
                    final float z = rz * this.regionSize.size * h;
                    this.context.translate(x, z, 0.0f);
                    region.getMesh().draw();
                    this.context.popMatrix();
                }
            }
        }
        this.context.popMatrix();
    }
    
    private CacheEntry<Tile> getAsync(final float x, final float z, final float zoom, final boolean filters) {
        final Tile tile;
        return new CacheEntry<Tile>(CacheEntry.computeAsync(() -> {
            tile = this.generator.createEmptyRegion(0, 0);
            tile.generate(this.heightmapCache, x, z, zoom);
            return tile;
        }, this.threadPool));
    }
}
