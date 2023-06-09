// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.pos;

public class PosUtil
{
    private static final long MASK = 4294967295L;
    
    public static long packMix(final int left, final float right) {
        return ((long)Float.floatToRawIntBits(right) & 0xFFFFFFFFL) | ((long)left & 0xFFFFFFFFL) << 32;
    }
    
    public static long packMix(final float left, final int right) {
        return ((long)right & 0xFFFFFFFFL) | ((long)Float.floatToRawIntBits(left) & 0xFFFFFFFFL) << 32;
    }
    
    public static long pack(final int left, final int right) {
        return ((long)right & 0xFFFFFFFFL) | ((long)left & 0xFFFFFFFFL) << 32;
    }
    
    public static long pack(final float left, final float right) {
        return pack((int)left, (int)right);
    }
    
    public static int unpackLeft(final long packed) {
        return (int)(packed >>> 32 & 0xFFFFFFFFL);
    }
    
    public static int unpackRight(final long packed) {
        return (int)(packed & 0xFFFFFFFFL);
    }
    
    public static long packf(final float left, final float right) {
        return ((long)Float.floatToRawIntBits(right) & 0xFFFFFFFFL) | ((long)Float.floatToRawIntBits(left) & 0xFFFFFFFFL) << 32;
    }
    
    public static float unpackLeftf(final long packed) {
        return Float.intBitsToFloat((int)(packed >>> 32 & 0xFFFFFFFFL));
    }
    
    public static float unpackRightf(final long packed) {
        return Float.intBitsToFloat((int)(packed & 0xFFFFFFFFL));
    }
    
    public static <T> void iterate(final int startX, final int startZ, final int width, final int depth, final T ctx, final Visitor<T> visitor) {
        for (int size = width * depth, i = 0; i < size; ++i) {
            final int dz = i / width;
            final int dx = i - dz * width;
            final int x = startX + dx;
            final int z = startZ + dz;
            visitor.visit(x, z, ctx);
        }
    }
    
    public static boolean contains(final int x, final int z, final int x1, final int z1, final int x2, final int z2) {
        return x >= x1 && x < x2 && z >= z1 && z < z2;
    }
    
    public static boolean contains(final float x, final float z, final float x1, final float z1, final float x2, final float z2) {
        return x >= x1 && x < x2 && z >= z1 && z < z2;
    }
    
    public interface Visitor<T>
    {
        void visit(final int p0, final int p1, final T p2);
    }
}
