// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine;

import com.terraforged.cereal.spec.DataSpecs;
import com.terraforged.engine.module.Ridge;

public class Engine
{
    public static final boolean ENFORCE_STABLE_OPTIONS;
    
    public static void init() {
    }
    
    static {
        ENFORCE_STABLE_OPTIONS = (System.getProperty("unstable") == null);
        DataSpecs.register(Ridge.spec());
    }
}
