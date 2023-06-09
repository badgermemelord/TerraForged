// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.gen;

import com.terraforged.engine.world.rivermap.river.Network;

public class GenRiver implements Comparable<GenRiver>
{
    public final float dx;
    public final float dz;
    public final float angle;
    public final float length;
    public final Network.Builder builder;
    
    public GenRiver(final Network.Builder branch, final float angle, final float dx, final float dz, final float length) {
        this.dx = dx;
        this.dz = dz;
        this.angle = angle;
        this.length = length;
        this.builder = branch;
    }
    
    @Override
    public int compareTo(final GenRiver o) {
        return Float.compare(this.angle, o.angle);
    }
}
