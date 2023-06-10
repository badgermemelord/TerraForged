//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.heightmap;

public class ControlPoints {
    public final float deepOcean;
    public final float shallowOcean;
    public final float beach;
    public final float coast;
    public final float coastMarker;
    public final float inland;

    public ControlPoints(com.terraforged.engine.settings.WorldSettings.ControlPoints points) {
        if (!validate(points)) {
            points = new com.terraforged.engine.settings.WorldSettings.ControlPoints();
        }

        this.inland = points.inland;
        this.coast = points.coast;
        this.beach = points.beach;
        this.shallowOcean = points.shallowOcean;
        this.deepOcean = points.deepOcean;
        this.coastMarker = this.coast + (this.inland - this.coast) / 2.0F;
    }

    public static boolean validate(com.terraforged.engine.settings.WorldSettings.ControlPoints points) {
        return points.inland <= 1.0F
                && points.inland > points.coast
                && points.coast > points.beach
                && points.beach > points.shallowOcean
                && points.shallowOcean > points.deepOcean
                && points.deepOcean >= 0.0F;
    }
}
