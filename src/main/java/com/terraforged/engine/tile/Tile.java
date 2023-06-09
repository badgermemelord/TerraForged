// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.Disposable;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.batch.Batcher;
import com.terraforged.engine.concurrent.cache.SafeCloseable;
import com.terraforged.engine.filter.Filterable;
import com.terraforged.engine.tile.chunk.*;
import com.terraforged.engine.tile.gen.TileResources;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.heightmap.Heightmap;
import com.terraforged.engine.world.heightmap.HeightmapCache;
import com.terraforged.engine.world.rivermap.Rivermap;
import com.terraforged.noise.util.NoiseUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Tile implements Disposable, SafeCloseable
{
    protected final int regionX;
    protected final int regionZ;
    protected final int chunkX;
    protected final int chunkZ;
    protected final int blockX;
    protected final int blockZ;
    protected final int border;
    protected final int chunkCount;
    protected final int size;
    protected final Size blockSize;
    protected final Size chunkSize;
    protected final Cell[] blocks;
    protected final GenChunk[] chunks;
    protected final Resource<Cell[]> blockResource;
    protected final Resource<GenChunk[]> chunkResource;
    protected final AtomicInteger active;
    protected final AtomicInteger disposed;
    protected final Listener<Tile> listener;
    
    public Tile(final int regionX, final int regionZ, final int size, final int borderChunks, final TileResources resources, final Listener<Tile> listener) {
        this.active = new AtomicInteger();
        this.disposed = new AtomicInteger();
        this.size = size;
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.listener = listener;
        this.chunkX = regionX << size;
        this.chunkZ = regionZ << size;
        this.blockX = Size.chunkToBlock(this.chunkX);
        this.blockZ = Size.chunkToBlock(this.chunkZ);
        this.border = borderChunks;
        this.chunkSize = Size.chunks(size, borderChunks);
        this.blockSize = Size.blocks(size, borderChunks);
        this.chunkCount = this.chunkSize.size * this.chunkSize.size;
        this.blockResource = resources.blocks.get(this.blockSize.arraySize);
        this.chunkResource = resources.chunks.get(this.chunkSize.arraySize);
        this.blocks = this.blockResource.get();
        this.chunks = this.chunkResource.get();
    }
    
    public int getGenerationSize() {
        return this.size;
    }
    
    @Override
    public void dispose() {
        if (this.disposed.incrementAndGet() >= this.chunkCount) {
            this.listener.onDispose(this);
        }
    }
    
    @Override
    public void close() {
        if (this.active.compareAndSet(0, -1)) {
            if (this.blockResource.isOpen()) {
                for (final Cell cell : this.blocks) {
                    if (cell != null) {
                        cell.reset();
                    }
                }
                this.blockResource.close();
            }
            if (this.chunkResource.isOpen()) {
                Arrays.fill(this.chunks, null);
                this.chunkResource.close();
            }
        }
    }
    
    public long getRegionId() {
        return getRegionId(this.getRegionX(), this.getRegionZ());
    }
    
    public int getRegionX() {
        return this.regionX;
    }
    
    public int getRegionZ() {
        return this.regionZ;
    }
    
    public int getBlockX() {
        return this.blockX;
    }
    
    public int getBlockZ() {
        return this.blockZ;
    }
    
    public int getOffsetChunks() {
        return this.border;
    }
    
    public int getChunkCount() {
        return this.chunks.length;
    }
    
    public int getBlockCount() {
        return this.blocks.length;
    }
    
    public Size getChunkSize() {
        return this.chunkSize;
    }
    
    public Size getBlockSize() {
        return this.blockSize;
    }
    
    public Filterable filterable() {
        return new FilterRegion();
    }
    
    public Cell getCell(final int blockX, final int blockZ) {
        final int relBlockX = this.blockSize.border + this.blockSize.mask(blockX);
        final int relBlockZ = this.blockSize.border + this.blockSize.mask(blockZ);
        final int index = this.blockSize.indexOf(relBlockX, relBlockZ);
        return this.blocks[index];
    }
    
    public Cell getRawCell(final int blockX, final int blockZ) {
        final int index = this.blockSize.indexOf(blockX, blockZ);
        return this.blocks[index];
    }
    
    public ChunkWriter getChunkWriter(final int chunkX, final int chunkZ) {
        final int index = this.chunkSize.indexOf(chunkX, chunkZ);
        return this.computeChunk(index, chunkX, chunkZ);
    }
    
    public ChunkReader getChunk(final int chunkX, final int chunkZ) {
        final int relChunkX = this.chunkSize.border + this.chunkSize.mask(chunkX);
        final int relChunkZ = this.chunkSize.border + this.chunkSize.mask(chunkZ);
        final int index = this.chunkSize.indexOf(relChunkX, relChunkZ);
        return this.chunks[index].open();
    }
    
    public void generate(final Consumer<ChunkWriter> consumer) {
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int cx = 0; cx < this.chunkSize.total; ++cx) {
                final int index = this.chunkSize.indexOf(cx, cz);
                final GenChunk chunk = this.computeChunk(index, cx, cz);
                consumer.accept(chunk);
            }
        }
    }
    
    public void generate(final Heightmap heightmap) {
        Rivermap riverMap = null;
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int cx = 0; cx < this.chunkSize.total; ++cx) {
                final int index = this.chunkSize.indexOf(cx, cz);
                final GenChunk chunk = this.computeChunk(index, cx, cz);
                for (int dz = 0; dz < 16; ++dz) {
                    for (int dx = 0; dx < 16; ++dx) {
                        final float x = (float)(chunk.getBlockX() + dx);
                        final float z = (float)(chunk.getBlockZ() + dz);
                        final Cell cell = chunk.genCell(dx, dz);
                        heightmap.applyBase(cell, x, z);
                        riverMap = Rivermap.get(cell, riverMap, heightmap);
                        heightmap.applyRivers(cell, x, z, riverMap);
                        heightmap.applyClimate(cell, x, z);
                    }
                }
            }
        }
    }
    
    public void generate(final HeightmapCache heightmap) {
        Rivermap riverMap = null;
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int cx = 0; cx < this.chunkSize.total; ++cx) {
                final int index = this.chunkSize.indexOf(cx, cz);
                final GenChunk chunk = this.computeChunk(index, cx, cz);
                for (int dz = 0; dz < 16; ++dz) {
                    for (int dx = 0; dx < 16; ++dx) {
                        final int x = chunk.getBlockX() + dx;
                        final int z = chunk.getBlockZ() + dz;
                        final Cell cell = chunk.genCell(dx, dz);
                        riverMap = heightmap.generate(cell, x, z, riverMap);
                    }
                }
            }
        }
    }
    
    public void generate(final Heightmap heightmap, final Batcher batcher) {
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int cx = 0; cx < this.chunkSize.total; ++cx) {
                final int index = this.chunkSize.indexOf(cx, cz);
                final GenChunk chunk = this.computeChunk(index, cx, cz);
                batcher.submit(new ChunkGenTask(chunk, heightmap));
            }
        }
    }
    
    public void generate(final Heightmap heightmap, final float offsetX, final float offsetZ, final float zoom) {
        Rivermap riverMap = null;
        final float translateX = offsetX - this.blockSize.size * zoom / 2.0f;
        final float translateZ = offsetZ - this.blockSize.size * zoom / 2.0f;
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int cx = 0; cx < this.chunkSize.total; ++cx) {
                final int index = this.chunkSize.indexOf(cx, cz);
                final GenChunk chunk = this.computeChunk(index, cx, cz);
                for (int dz = 0; dz < 16; ++dz) {
                    for (int dx = 0; dx < 16; ++dx) {
                        final float x = (chunk.getBlockX() + dx) * zoom + translateX;
                        final float z = (chunk.getBlockZ() + dz) * zoom + translateZ;
                        final Cell cell = chunk.genCell(dx, dz);
                        heightmap.applyBase(cell, x, z);
                        riverMap = Rivermap.get(cell, riverMap, heightmap);
                        heightmap.applyRivers(cell, x, z, riverMap);
                        heightmap.applyClimate(cell, x, z);
                    }
                }
            }
        }
    }
    
    public void generate(final HeightmapCache heightmap, final float offsetX, final float offsetZ, final float zoom) {
        Rivermap riverMap = null;
        final float translateX = offsetX - this.blockSize.size * zoom / 2.0f;
        final float translateZ = offsetZ - this.blockSize.size * zoom / 2.0f;
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int cx = 0; cx < this.chunkSize.total; ++cx) {
                final int index = this.chunkSize.indexOf(cx, cz);
                final GenChunk chunk = this.computeChunk(index, cx, cz);
                for (int dz = 0; dz < 16; ++dz) {
                    for (int dx = 0; dx < 16; ++dx) {
                        final float x = (chunk.getBlockX() + dx) * zoom + translateX;
                        final float z = (chunk.getBlockZ() + dz) * zoom + translateZ;
                        final int px = NoiseUtil.floor(x);
                        final int pz = NoiseUtil.floor(z);
                        final Cell cell = chunk.genCell(dx, dz);
                        riverMap = heightmap.generate(cell, px, pz, riverMap);
                    }
                }
            }
        }
    }
    
    public void generate(final Heightmap heightmap, final Batcher batcher, final float offsetX, final float offsetZ, final float zoom) {
        final float translateX = offsetX - this.blockSize.size * zoom / 2.0f;
        final float translateZ = offsetZ - this.blockSize.size * zoom / 2.0f;
        batcher.size(this.chunkSize.total * this.chunkSize.total);
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int cx = 0; cx < this.chunkSize.total; ++cx) {
                final int index = this.chunkSize.indexOf(cx, cz);
                final GenChunk chunk = this.computeChunk(index, cx, cz);
                batcher.submit(new ChunkGenTask.Zoom(chunk, heightmap, translateX, translateZ, zoom));
            }
        }
    }
    
    public void generateArea(final Heightmap heightmap, final Batcher batcher, final int batchCount) {
        batcher.size(batchCount * batchCount);
        final int batchSize = getBatchSize(batchCount, this.chunkSize);
        for (int dz = 0; dz < batchCount; ++dz) {
            final int cz = dz * batchSize;
            for (int dx = 0; dx < batchCount; ++dx) {
                final int cx = dx * batchSize;
                batcher.submit(new ChunkBatchTask(cx, cz, batchSize, this, heightmap));
            }
        }
    }
    
    public void generateArea(final Heightmap heightmap, final Batcher batcher, final int batchCount, final float offsetX, final float offsetZ, final float zoom) {
        batcher.size(batchCount * batchCount);
        final int batchSize = getBatchSize(batchCount, this.chunkSize);
        final float translateX = offsetX - this.blockSize.size * zoom / 2.0f;
        final float translateZ = offsetZ - this.blockSize.size * zoom / 2.0f;
        for (int dz = 0; dz < batchCount; ++dz) {
            final int cz = dz * batchSize;
            for (int dx = 0; dx < batchCount; ++dx) {
                final int cx = dx * batchSize;
                batcher.submit(new ChunkBatchTask.Zoom(cx, cz, batchSize, this, heightmap, translateX, translateZ, zoom));
            }
        }
    }
    
    public void generateAreaStriped(final Heightmap heightmap, final Batcher batcher, final int sections) {
        batcher.size(this.chunkSize.total * sections);
        final int sectionLength = getBatchSize(sections, this.chunkSize);
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int s = 0; s < sections; ++s) {
                final int cx = s * sectionLength;
                batcher.submit(new ChunkStripeBatchTask(cx, cz, sectionLength, this, heightmap));
            }
        }
    }
    
    public void generateAreaStriped(final Heightmap heightmap, final Batcher batcher, final int sections, final float offsetX, final float offsetZ, final float zoom) {
        batcher.size(this.chunkSize.total * sections);
        final int sectionLength = getBatchSize(sections, this.chunkSize);
        final float translateX = offsetX - this.blockSize.size * zoom / 2.0f;
        final float translateZ = offsetZ - this.blockSize.size * zoom / 2.0f;
        for (int cz = 0; cz < this.chunkSize.total; ++cz) {
            for (int s = 0; s < sections; ++s) {
                final int cx = s * sectionLength;
                batcher.submit(new ChunkStripeBatchTask.Zoom(cx, cz, sectionLength, this, heightmap, translateX, translateZ, zoom));
            }
        }
    }
    
    public void iterate(final Cell.Visitor visitor) {
        for (int dz = 0; dz < this.blockSize.size; ++dz) {
            final int z = this.blockSize.border + dz;
            for (int dx = 0; dx < this.blockSize.size; ++dx) {
                final int x = this.blockSize.border + dx;
                final int index = this.blockSize.indexOf(x, z);
                final Cell cell = this.blocks[index];
                visitor.visit(cell, dx, dz);
            }
        }
    }
    
    public void generate(final Cell.Visitor visitor) {
        for (int dz = 0; dz < this.blockSize.size; ++dz) {
            final int z = this.blockSize.border + dz;
            for (int dx = 0; dx < this.blockSize.size; ++dx) {
                final int x = this.blockSize.border + dx;
                final int index = this.blockSize.indexOf(x, z);
                final Cell cell = this.computeCell(index);
                visitor.visit(cell, dx, dz);
            }
        }
    }
    
    protected GenChunk computeChunk(final int index, final int chunkX, final int chunkZ) {
        GenChunk chunk = this.chunks[index];
        if (chunk == null) {
            chunk = new GenChunk(chunkX, chunkZ);
            this.chunks[index] = chunk;
        }
        return chunk;
    }
    
    protected Cell computeCell(final int index) {
        Cell cell = this.blocks[index];
        if (cell == null) {
            cell = new Cell();
            this.blocks[index] = cell;
        }
        return cell;
    }
    
    protected static int getBatchSize(final int batchCount, final Size chunkSize) {
        int batchSize = chunkSize.total / batchCount;
        if (batchSize * batchCount < chunkSize.total) {
            ++batchSize;
        }
        return batchSize;
    }
    
    public static long getRegionId(final int regionX, final int regionZ) {
        return PosUtil.pack(regionX, regionZ);
    }
    
    public static int getRegionX(final long id) {
        return PosUtil.unpackLeft(id);
    }
    
    public static int getRegionZ(final long id) {
        return PosUtil.unpackRight(id);
    }
    
    public class GenChunk implements ChunkReader, ChunkWriter
    {
        private final int chunkX;
        private final int chunkZ;
        private final int blockX;
        private final int blockZ;
        private final int regionBlockX;
        private final int regionBlockZ;
        
        protected GenChunk(final int regionChunkX, final int regionChunkZ) {
            this.regionBlockX = regionChunkX << 4;
            this.regionBlockZ = regionChunkZ << 4;
            this.chunkX = Tile.this.chunkX + regionChunkX - Tile.this.getOffsetChunks();
            this.chunkZ = Tile.this.chunkZ + regionChunkZ - Tile.this.getOffsetChunks();
            this.blockX = this.chunkX << 4;
            this.blockZ = this.chunkZ << 4;
        }
        
        public GenChunk open() {
            Tile.this.active.getAndIncrement();
            return this;
        }
        
        @Override
        public void close() {
            Tile.this.active.decrementAndGet();
        }
        
        @Override
        public void dispose() {
            Tile.this.dispose();
        }
        
        @Override
        public int getChunkX() {
            return this.chunkX;
        }
        
        @Override
        public int getChunkZ() {
            return this.chunkZ;
        }
        
        @Override
        public int getBlockX() {
            return this.blockX;
        }
        
        @Override
        public int getBlockZ() {
            return this.blockZ;
        }
        
        @Override
        public Cell getCell(final int blockX, final int blockZ) {
            final int relX = this.regionBlockX + (blockX & 0xF);
            final int relZ = this.regionBlockZ + (blockZ & 0xF);
            final int index = Tile.this.blockSize.indexOf(relX, relZ);
            return Tile.this.blocks[index];
        }
        
        @Override
        public Cell genCell(final int blockX, final int blockZ) {
            final int relX = this.regionBlockX + (blockX & 0xF);
            final int relZ = this.regionBlockZ + (blockZ & 0xF);
            final int index = Tile.this.blockSize.indexOf(relX, relZ);
            return Tile.this.computeCell(index);
        }
    }
    
    protected class FilterRegion implements Filterable
    {
        @Override
        public int getBlockX() {
            return Tile.this.blockX;
        }
        
        @Override
        public int getBlockZ() {
            return Tile.this.blockZ;
        }
        
        @Override
        public Size getSize() {
            return Tile.this.blockSize;
        }
        
        @Override
        public Cell[] getBacking() {
            return Tile.this.blocks;
        }
        
        @Override
        public Cell getCellRaw(final int x, final int z) {
            final int index = Tile.this.blockSize.indexOf(x, z);
            if (index < 0 || index >= Tile.this.blockSize.arraySize) {
                return Cell.empty();
            }
            return Tile.this.blocks[index];
        }
    }
}
