// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile;

public class Size
{
    public final int size;
    public final int total;
    public final int border;
    public final int arraySize;
    public final int lowerBorder;
    public final int upperBorder;
    private final int mask;
    
    public Size(final int size, final int border) {
        this.size = size;
        this.mask = size - 1;
        this.border = border;
        this.total = size + 2 * border;
        this.lowerBorder = border;
        this.upperBorder = border + size;
        this.arraySize = this.total * this.total;
    }
    
    public int mask(final int i) {
        return i & this.mask;
    }
    
    public int indexOf(final int x, final int z) {
        return z * this.total + x;
    }
    
    public static int chunkToBlock(final int i) {
        return i << 4;
    }
    
    public static int blockToChunk(final int i) {
        return i >> 4;
    }
    
    public static int count(final int minX, final int minZ, final int maxX, final int maxZ) {
        final int dx = maxX - minX;
        final int dz = maxZ - minZ;
        return dx * dz;
    }
    
    public static Size chunks(final int factor, final int borderChunks) {
        final int chunks = 1 << factor;
        return new Size(chunks, borderChunks);
    }
    
    public static Size blocks(final int factor, final int borderChunks) {
        final int chunks = 1 << factor;
        final int blocks = chunks << 4;
        final int borderBlocks = borderChunks << 4;
        return new Size(blocks, borderBlocks);
    }
}
