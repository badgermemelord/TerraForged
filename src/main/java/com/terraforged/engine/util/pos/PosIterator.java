// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.pos;

public class PosIterator
{
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final int size;
    private int x;
    private int y;
    private int z;
    private int index;
    
    public PosIterator(final int x, final int y, final int z, final int width, final int height, final int length) {
        this.index = -1;
        this.x = x - 1;
        this.y = y;
        this.z = z;
        this.minX = x;
        this.minZ = z;
        this.maxX = x + width;
        this.maxY = y + height;
        this.maxZ = z + length;
        this.size = width * height * length - 1;
    }
    
    public boolean next() {
        if (this.x + 1 < this.maxX) {
            ++this.x;
            ++this.index;
            return true;
        }
        if (this.z + 1 < this.maxZ) {
            this.x = this.minX;
            ++this.z;
            ++this.index;
            return true;
        }
        if (this.y + 1 < this.maxY) {
            this.x = this.minX - 1;
            this.z = this.minZ;
            ++this.y;
            return true;
        }
        return false;
    }
    
    public int size() {
        return this.size;
    }
    
    public int index() {
        return this.index;
    }
    
    public int x() {
        return this.x;
    }
    
    public int y() {
        return this.y;
    }
    
    public int z() {
        return this.z;
    }
    
    public static PosIterator radius2D(final int x, final int z, final int radius) {
        final int startX = x - radius;
        final int startZ = z - radius;
        final int size = radius * 2 + 1;
        return new PosIterator(startX, 0, startZ, size, 0, size);
    }
    
    public static PosIterator radius3D(final int x, final int y, final int z, final int radius) {
        final int startX = x - radius;
        final int startY = y - radius;
        final int startZ = z - radius;
        final int size = radius * 2 + 1;
        return new PosIterator(startX, startY, startZ, size, size, size);
    }
    
    public static PosIterator area(final int x, final int z, final int width, final int length) {
        return new PosIterator(x, 0, z, width, 0, length);
    }
    
    public static PosIterator volume3D(final int x, final int y, final int z, final int width, final int height, final int length) {
        return new PosIterator(x, y, z, width, height, length);
    }
    
    public static PosIterator range2D(final int minX, final int minZ, final int maxX, final int maxZ) {
        final int width = maxX - minX;
        final int length = maxZ - minZ;
        return new PosIterator(minX, 0, minZ, width, 0, length);
    }
    
    public static PosIterator range2D(final int minX, final int minY, final int minZ, final int maxX, final int maxY, final int maxZ) {
        final int width = 1 + maxX - minX;
        final int height = 1 + maxY - minY;
        final int length = 1 + maxZ - minZ;
        return new PosIterator(minX, minY, minZ, width, height, length);
    }
}
