//
// Source code recreated from a .class file by Quiltflower
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

public class RenderWorld {
    private final int regionCount;
    private final Size regionSize;
    private final RenderAPI context;
    private final ThreadPool threadPool;
    private final RegionRenderer renderer;
    private final TileGenerator generator;
    private final RenderRegion[] view;
    private final LazyCallable<RenderRegion>[] queue;
    private final HeightmapCache heightmapCache;

    public RenderWorld(ThreadPool threadPool, TileGenerator generator, RenderAPI context, RenderSettings settings, int regionCount, int regionSize) {
        this.threadPool = threadPool;
        this.context = context;
        this.generator = generator;
        this.regionCount = regionCount;
        this.renderer = new RegionRenderer(context, settings);
        this.regionSize = Size.blocks(regionSize, 0);
        this.queue = new LazyCallable[regionCount * regionCount];
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
        for(LazyCallable<?> entry : this.queue) {
            if (entry != null) {
                return true;
            }
        }

        return false;
    }

    public Cell getCenter() {
        float cx = (float)this.regionCount / 2.0F;
        float cz = (float)this.regionCount / 2.0F;
        int rx = (int)cx;
        int rz = (int)cz;
        int index = rx + this.regionCount * rz;
        RenderRegion renderRegion = this.view[index];
        if (renderRegion == null) {
            return Cell.empty();
        } else {
            float ox = cx - (float)rx;
            float oz = cz - (float)rz;
            Tile tile = renderRegion.getTile();
            int dx = (int)((float)tile.getBlockSize().size * ox);
            int dz = (int)((float)tile.getBlockSize().size * oz);
            return tile.getCell(dx, dz);
        }
    }

    public void redraw() {
        for(RenderRegion region : this.view) {
            if (region != null) {
                this.renderer.render(region);
            }
        }
    }

    public void refresh() {
        for(LazyCallable<?> entry : this.queue) {
            if (entry != null && !entry.isDone()) {
                return;
            }
        }

        for(int i = 0; i < this.queue.length; ++i) {
            LazyCallable<RenderRegion> entry = this.queue[i];
            if (entry != null && entry.isDone()) {
                this.queue[i] = null;
                this.view[i] = (RenderRegion)entry.get();
            }
        }
    }

    public void update(float x, float y, float zoom, boolean filters) {
        this.renderer.getSettings().zoom = zoom;
        this.renderer.getSettings().resolution = this.getResolution();
        float factor = this.regionCount > 1 ? ((float)this.regionCount - 1.0F) / (float)this.regionCount : 0.0F;
        float offset = (float)this.regionSize.size * zoom * factor;

        for(int rz = 0; rz < this.regionCount; ++rz) {
            for(int rx = 0; rx < this.regionCount; ++rx) {
                int index = rx + rz * this.regionCount;
                float px = x + (float)(rx * this.regionSize.size) * zoom - offset;
                float py = y + (float)(rz * this.regionSize.size) * zoom - offset;
                this.queue[index] = this.generator.getTile(px, py, zoom, filters).then(this.threadPool, this.renderer::render);
            }
        }
    }

    public void render() {
        int resolution = this.getResolution();
        float w = (float)this.renderer.getSettings().width / (float)(resolution - 1);
        float h = (float)this.renderer.getSettings().width / (float)(resolution - 1);
        float offsetX = (float)(this.regionSize.size * this.regionCount) * w / 2.0F;
        float offsetY = (float)(this.regionSize.size * this.regionCount) * w / 2.0F;
        this.context.pushMatrix();
        this.context.translate(-offsetX, -offsetY, 0.0F);

        for(int rz = 0; rz < this.regionCount; ++rz) {
            for(int rx = 0; rx < this.regionCount; ++rx) {
                int index = rx + rz * this.regionCount;
                RenderRegion region = this.view[index];
                if (region != null) {
                    this.context.pushMatrix();
                    float x = (float)(rx * this.regionSize.size) * w;
                    float z = (float)(rz * this.regionSize.size) * h;
                    this.context.translate(x, z, 0.0F);
                    region.getMesh().draw();
                    this.context.popMatrix();
                }
            }
        }

        this.context.popMatrix();
    }

    private CacheEntry<Tile> getAsync(float x, float z, float zoom, boolean filters) {
        return new CacheEntry(CacheEntry.computeAsync(() -> {
            Tile tile = this.generator.createEmptyRegion(0, 0);
            tile.generate(this.heightmapCache, x, z, zoom);
            return tile;
        }, this.threadPool));
    }
}
