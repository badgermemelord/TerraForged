//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Line implements Module {
    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;
    private final float dx;
    private final float dy;
    private final float orthX1;
    private final float orthY1;
    private final float orthX2;
    private final float orthY2;
    private final float length2;
    private final float featherBias;
    private final float featherScale;
    private final Module fadeIn;
    private final Module fadeOut;
    private final Module radius;

    public Line(float x1, float y1, float x2, float y2, Module radius2, Module fadeIn, Module fadeOut, float feather) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.orthX1 = x1 + (y2 - y1);
        this.orthY1 = y1 + (x1 - x2);
        this.orthX2 = x2 + (y2 - y1);
        this.orthY2 = y2 + (x1 - x2);
        this.dx = x2 - x1;
        this.dy = y2 - y1;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.radius = radius2;
        this.featherScale = feather;
        this.featherBias = 1.0F - feather;
        this.length2 = this.dx * this.dx + this.dy * this.dy;
    }

    public String getSpecName() {
        return "Line";
    }

    public float getValue(float x, float y) {
        float widthMod = this.getWidthModifier(x, y);
        return this.getValue(x, y, widthMod);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Line line = (Line)o;
            if (Float.compare(line.x1, this.x1) != 0) {
                return false;
            } else if (Float.compare(line.y1, this.y1) != 0) {
                return false;
            } else if (Float.compare(line.x2, this.x2) != 0) {
                return false;
            } else if (Float.compare(line.y2, this.y2) != 0) {
                return false;
            } else if (Float.compare(line.dx, this.dx) != 0) {
                return false;
            } else if (Float.compare(line.dy, this.dy) != 0) {
                return false;
            } else if (Float.compare(line.orthX1, this.orthX1) != 0) {
                return false;
            } else if (Float.compare(line.orthY1, this.orthY1) != 0) {
                return false;
            } else if (Float.compare(line.orthX2, this.orthX2) != 0) {
                return false;
            } else if (Float.compare(line.orthY2, this.orthY2) != 0) {
                return false;
            } else if (Float.compare(line.length2, this.length2) != 0) {
                return false;
            } else if (Float.compare(line.featherBias, this.featherBias) != 0) {
                return false;
            } else if (Float.compare(line.featherScale, this.featherScale) != 0) {
                return false;
            } else if (!this.fadeIn.equals(line.fadeIn)) {
                return false;
            } else {
                return !this.fadeOut.equals(line.fadeOut) ? false : this.radius.equals(line.radius);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.x1 != 0.0F ? Float.floatToIntBits(this.x1) : 0;
        result = 31 * result + (this.y1 != 0.0F ? Float.floatToIntBits(this.y1) : 0);
        result = 31 * result + (this.x2 != 0.0F ? Float.floatToIntBits(this.x2) : 0);
        result = 31 * result + (this.y2 != 0.0F ? Float.floatToIntBits(this.y2) : 0);
        result = 31 * result + (this.dx != 0.0F ? Float.floatToIntBits(this.dx) : 0);
        result = 31 * result + (this.dy != 0.0F ? Float.floatToIntBits(this.dy) : 0);
        result = 31 * result + (this.orthX1 != 0.0F ? Float.floatToIntBits(this.orthX1) : 0);
        result = 31 * result + (this.orthY1 != 0.0F ? Float.floatToIntBits(this.orthY1) : 0);
        result = 31 * result + (this.orthX2 != 0.0F ? Float.floatToIntBits(this.orthX2) : 0);
        result = 31 * result + (this.orthY2 != 0.0F ? Float.floatToIntBits(this.orthY2) : 0);
        result = 31 * result + (this.length2 != 0.0F ? Float.floatToIntBits(this.length2) : 0);
        result = 31 * result + (this.featherBias != 0.0F ? Float.floatToIntBits(this.featherBias) : 0);
        result = 31 * result + (this.featherScale != 0.0F ? Float.floatToIntBits(this.featherScale) : 0);
        result = 31 * result + this.fadeIn.hashCode();
        result = 31 * result + this.fadeOut.hashCode();
        return 31 * result + this.radius.hashCode();
    }

    public float getValue(float x, float y, float widthModifier) {
        return this.getValue(x, y, 0.0F, widthModifier);
    }

    public float getValue(float x, float y, float minWidth2, float widthModifier) {
        float dist2 = this.getDistance2(x, y);
        float radius2 = minWidth2 + this.radius.getValue(x, y) * widthModifier;
        if (dist2 > radius2) {
            return 0.0F;
        } else {
            float value = dist2 / radius2;
            if (this.featherScale == 0.0F) {
                return 1.0F - value;
            } else {
                float feather = this.featherBias + widthModifier * this.featherScale;
                return (1.0F - value) * feather;
            }
        }
    }

    public boolean clipStart(float x, float y) {
        return sign(x, y, this.x1, this.y1, this.orthX1, this.orthY1) > 0;
    }

    public boolean clipEnd(float x, float y) {
        return sign(x, y, this.x2, this.y2, this.orthX2, this.orthY2) < 0;
    }

    public float getWidthModifier(float x, float y) {
        float d1 = dist2(x, y, this.x1, this.y1);
        if (d1 == 0.0F) {
            return 0.0F;
        } else {
            float d2 = dist2(x, y, this.x2, this.y2);
            if (d2 == 0.0F) {
                return 0.0F;
            } else {
                float fade = 1.0F;
                float in = this.fadeIn.getValue(x, y);
                float out = this.fadeOut.getValue(x, y);
                if (in > 0.0F) {
                    float dist = in * this.length2;
                    if (d1 < dist) {
                        fade *= d1 / dist;
                    }
                }

                if (out > 0.0F) {
                    float dist = out * this.length2;
                    if (d2 < dist) {
                        fade *= d2 / dist;
                    }
                }

                return fade;
            }
        }
    }

    private float getDistance2(float x, float y) {
        float t = (x - this.x1) * this.dx + (y - this.y1) * this.dy;
        float s = NoiseUtil.clamp(t / this.length2, 0.0F, 1.0F);
        float ix = this.x1 + s * this.dx;
        float iy = this.y1 + s * this.dy;
        return dist2(x, y, ix, iy);
    }

    public static float dist2(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    public static int sign(float x, float y, float x1, float y1, float x2, float y2) {
        float value = (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1);
        if (value == 0.0F) {
            return 0;
        } else {
            return value < 0.0F ? -1 : 1;
        }
    }

    public static boolean intersect(float ax1, float ay1, float ax2, float ay2, float bx1, float by1, float bx2, float by2) {
        return relativeCCW(ax1, ay1, ax2, ay2, bx1, by1) * relativeCCW(ax1, ay1, ax2, ay2, bx2, by2) <= 0
                && relativeCCW(bx1, by1, bx2, by2, ax1, ay1) * relativeCCW(bx1, by1, bx2, by2, ax2, ay2) <= 0;
    }

    public static float distanceOnLine(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        return v / (dx * dx + dy * dy);
    }

    public static float distance2Line(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        v /= dx * dx + dy * dy;
        if (!(v < 0.0F) && !(v > 1.0F)) {
            float ox = ax + dx * v;
            float oy = ay + dy * v;
            return NoiseUtil.dist2(x, y, ox, oy);
        } else {
            return -1.0F;
        }
    }

    public static float distance2LineIncEnds(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        v /= dx * dx + dy * dy;
        if (v < 0.0F) {
            return dist2(x, y, ax, ay);
        } else {
            return v > 1.0F ? dist2(x, y, bx, by) : dist2(x, y, ax + dx * v, ay + dy * v);
        }
    }

    private static int relativeCCW(float x1, float y1, float x2, float y2, float px, float py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = (double)(px * y2 - py * x2);
        if (ccw == 0.0) {
            ccw = (double)(px * x2 + py * y2);
            if (ccw > 0.0) {
                px -= x2;
                py -= y2;
                ccw = (double)(px * x2 + py * y2);
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }

        return ccw < 0.0 ? -1 : (ccw > 0.0 ? 1 : 0);
    }

    private static Line create(DataObject data, DataSpec<Line> spec, Context context) {
        return new Line(
                spec.get("x1", data, DataValue::asFloat),
                spec.get("y1", data, DataValue::asFloat),
                spec.get("x2", data, DataValue::asFloat),
                spec.get("y2", data, DataValue::asFloat),
                (Module)spec.get("radius", data, Module.class),
                (Module)spec.get("fade_in", data, Module.class),
                (Module)spec.get("fade_out", data, Module.class),
                spec.get("feather", data, DataValue::asFloat)
        );
    }

    public static DataSpec<Line> spec() {
        return DataSpec.builder("Line", Line.class, Line::create)
                .add("x1", 0.0F, l -> l.x1)
                .add("y1", 0.0F, l -> l.y1)
                .add("x2", 0.0F, l -> l.x2)
                .add("y2", 0.0F, l -> l.y2)
                .add("feather", 0.0F, l -> l.featherScale)
                .addObj("radius", Module.class, l -> l.radius)
                .addObj("fade_in", Module.class, l -> l.fadeIn)
                .addObj("fade_out", Module.class, l -> l.fadeOut)
                .build();
    }
}
