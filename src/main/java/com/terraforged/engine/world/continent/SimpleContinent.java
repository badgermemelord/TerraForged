//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.continent;

public interface SimpleContinent extends Continent {
    float getEdgeValue(float var1, float var2);

    default float getDistanceToEdge(int cx, int cz, float dx, float dy) {
        return 1.0F;
    }

    default float getDistanceToOcean(int cx, int cz, float dx, float dy) {
        return 1.0F;
    }
}
