//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.render;

import com.terraforged.engine.concurrent.task.LazyCallable;
import com.terraforged.engine.concurrent.thread.ThreadPool;
import com.terraforged.engine.concurrent.thread.ThreadPools;
import com.terraforged.engine.tile.Size;
import com.terraforged.engine.tile.api.TileProvider;
import com.terraforged.engine.tile.gen.TileGenerator;
import com.terraforged.engine.util.RollingGrid;
import com.terraforged.engine.util.RollingGrid.Generator;
import com.terraforged.engine.util.pos.PosIterator;

public class RenderWorld2 implements Generator<RenderWorld2.RegionHolder> {
    private final int factor;
    private final Size regionSize;
    private final TileProvider cache;
    private final TileGenerator generator;
    private final RenderAPI context;
    private final RegionRenderer renderer;
    private final RollingGrid<RenderWorld2.RegionHolder> world;
    private final ThreadPool threadPool = ThreadPools.createDefault();
    private boolean first = true;

    public RenderWorld2(TileGenerator generator, RenderAPI context, RenderSettings settings, int regionCount, int regionSize) {
        this.context = context;
        this.factor = regionSize;
        this.generator = generator;
        this.cache = generator.cached();
        this.regionSize = Size.blocks(regionSize, 0);
        this.renderer = new RegionRenderer(context, settings);
        this.world = new RollingGrid(regionCount, x$0 -> new RenderWorld2.RegionHolder[x$0], this);
    }

    public boolean isBusy() {
        for(RenderWorld2.RegionHolder h : this.world.getIterator()) {
            if (h != null && !h.region.isDone()) {
                return true;
            }
        }

        return false;
    }

    public int getResolution() {
        return this.regionSize.total * this.world.getSize();
    }

    public int blockToRegion(int value) {
        return value >> this.factor;
    }

    public void init(int centerX, int centerZ) {
        this.renderer.getSettings().zoom = 1.0F;
        this.renderer.getSettings().resolution = this.regionSize.total;
        PosIterator iterator = PosIterator.area(0, 0, this.world.getSize(), this.world.getSize());

        while(iterator.next()) {
            RenderWorld2.RegionHolder holder = this.generate(iterator.x(), iterator.z());
            this.world.set(iterator.x(), iterator.z(), holder);
        }
    }

    public void move(int centerX, int centerZ) {
        if (this.first) {
            this.first = false;
            this.init(centerX, centerZ);
        } else {
            this.renderer.getSettings().zoom = 1.0F;
            this.renderer.getSettings().resolution = this.regionSize.total;
            this.world.move(centerX, centerZ);
        }
    }

    public void render() {
        int resolution = this.regionSize.total;
        float w = (float)this.renderer.getSettings().width * 1.0F / (float)(resolution - 1);
        float h = (float)this.renderer.getSettings().width * 1.0F / (float)(resolution - 1);
        float offsetX = (float)(this.world.getSize() * this.regionSize.size) * w / 2.0F;
        float offsetZ = (float)(this.world.getSize() * this.regionSize.size) * h / 2.0F;
        this.context.pushMatrix();
        this.context.translate(-offsetX, -offsetZ, 1000.0F);
        PosIterator iterator = PosIterator.area(0, 0, this.world.getSize(), this.world.getSize());

        while(iterator.next()) {
            RenderWorld2.RegionHolder holder = (RenderWorld2.RegionHolder)this.world.get(iterator.x(), iterator.z());
            if (holder != null && holder.region.isDone()) {
                int relX = iterator.x();
                int relZ = iterator.z();
                float startX = (float)(relX * this.regionSize.size) * w;
                float startZ = (float)(relZ * this.regionSize.size) * h;
                RenderRegion region = (RenderRegion)holder.region.get();
                this.context.pushMatrix();
                this.context.translate(startX, startZ, 0.0F);
                region.getMesh().draw();
                this.context.popMatrix();
            }
        }

        this.context.popMatrix();
    }

    public RenderWorld2.RegionHolder generate(int x, int z) {
        return new RenderWorld2.RegionHolder(this.generator.getTile(x, z).then(this.threadPool, this.renderer::render));
    }

    public static class RegionHolder {
        private final LazyCallable<RenderRegion> region;

        private RegionHolder(LazyCallable<RenderRegion> region) {
            this.region = region;
        }
    }
}
