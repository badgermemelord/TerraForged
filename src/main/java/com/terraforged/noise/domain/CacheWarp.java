// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.domain;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;

public class CacheWarp implements Domain
{
    private final Domain domain;
    private boolean cached;
    private float cachedX;
    private float cachedY;
    private float x;
    private float y;
    
    public CacheWarp(final Domain domain) {
        this.cached = false;
        this.domain = domain;
    }
    
    @Override
    public String getSpecName() {
        return "CacheWarp";
    }
    
    @Override
    public float getOffsetX(final float x, final float y) {
        if (this.cached && x == this.x && y == this.y) {
            return this.cachedX;
        }
        this.x = x;
        this.y = y;
        return this.cachedX = this.domain.getOffsetX(x, y);
    }
    
    @Override
    public float getOffsetY(final float x, final float y) {
        if (this.cached && x == this.x && y == this.y) {
            return this.cachedY;
        }
        this.x = x;
        this.y = y;
        return this.cachedY = this.domain.getOffsetY(x, y);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final CacheWarp cacheWarp = (CacheWarp)o;
        return this.cached == cacheWarp.cached && Float.compare(cacheWarp.cachedX, this.cachedX) == 0 && Float.compare(cacheWarp.cachedY, this.cachedY) == 0 && Float.compare(cacheWarp.x, this.x) == 0 && Float.compare(cacheWarp.y, this.y) == 0 && this.domain.equals(cacheWarp.domain);
    }
    
    @Override
    public int hashCode() {
        int result = this.domain.hashCode();
        result = 31 * result + (this.cached ? 1 : 0);
        result = 31 * result + ((this.cachedX != 0.0f) ? Float.floatToIntBits(this.cachedX) : 0);
        result = 31 * result + ((this.cachedY != 0.0f) ? Float.floatToIntBits(this.cachedY) : 0);
        result = 31 * result + ((this.x != 0.0f) ? Float.floatToIntBits(this.x) : 0);
        result = 31 * result + ((this.y != 0.0f) ? Float.floatToIntBits(this.y) : 0);
        return result;
    }
    
    private static CacheWarp create(final DataObject data, final DataSpec<?> spec, final Context context) {
        return new CacheWarp(spec.get("domain", data, Domain.class, context));
    }
    
    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("CacheWarp", CacheWarp.class, CacheWarp::create).addObj("domain", Domain.class, w -> w.domain).build();
    }
}
