// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.geology;

import java.util.ArrayList;
import java.util.List;

public class Geology<T>
{
    private final Module selector;
    private final List<Strata<T>> backing;
    
    public Geology(final Module selector) {
        this.backing = new ArrayList<Strata<T>>();
        this.selector = selector;
    }
    
    public Geology<T> add(final Geology<T> geology) {
        this.backing.addAll(geology.backing);
        return this;
    }
    
    public Geology<T> add(final Strata<T> strata) {
        this.backing.add(strata);
        return this;
    }
    
    public Strata<T> getStrata(final float x, final int y) {
        final float noise = this.selector.getValue(x, (float)y);
        return this.getStrata(noise);
    }
    
    public Strata<T> getStrata(final float value) {
        int index = (int)(value * this.backing.size());
        index = Math.min(this.backing.size() - 1, index);
        return this.backing.get(index);
    }
}
