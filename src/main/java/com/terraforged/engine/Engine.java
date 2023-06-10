//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine;

import com.terraforged.cereal.spec.DataSpecs;
import com.terraforged.engine.module.Ridge;

public class Engine {
    public static final boolean ENFORCE_STABLE_OPTIONS = System.getProperty("unstable") == null;

    public Engine() {
    }

    public static void init() {
    }

    static {
        DataSpecs.register(Ridge.spec());
    }
}
