// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.render;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.tile.Tile;

public class RegionRenderer
{
    public static final float RENDER_SCALE = 1.0f;
    private final RenderSettings settings;
    private final RenderAPI context;
    
    public RegionRenderer(final RenderAPI context, final RenderSettings settings) {
        this.context = context;
        this.settings = settings;
    }
    
    public RenderSettings getSettings() {
        return this.settings;
    }
    
    public RenderRegion render(final Tile tile) {
        final RenderRegion renderRegion = new RenderRegion(tile);
        this.render(renderRegion);
        return renderRegion;
    }
    
    public void render(final RenderRegion region) {
        region.clear();
        final int resolution = this.settings.resolution;
        final float w = this.settings.width / (resolution - 1.0f);
        final float h = this.settings.width / (resolution - 1.0f);
        final float unit = w / this.settings.zoom;
        final RenderBuffer shape = this.context.createBuffer();
        shape.beginQuads();
        shape.noFill();
        for (int dy = 0; dy < resolution; ++dy) {
            for (int dx = 0; dx < resolution; ++dx) {
                this.draw(shape, region.getTile(), dx, dy, resolution, w, h, unit);
            }
        }
        shape.endQuads();
        region.setMesh(shape);
    }
    
    private void draw(final RenderBuffer shape, final Tile tile, final int dx, final int dz, final int resolution, final float w, final float h, final float unit) {
        final Cell cell = tile.getCell(dx, dz);
        if (cell == null) {
            return;
        }
        final float height = cell.value * this.settings.levels.worldHeight;
        final float x = dx * w;
        final float z = dz * h;
        final int y = this.getY(height, unit);
        this.settings.renderMode.fillColor(cell, height, shape, this.settings);
        shape.vertex(x, z, (float)y);
        shape.vertex(x + w, z, (float)y);
        shape.vertex(x + w, z + w, (float)y);
        shape.vertex(x, z + w, (float)y);
        if (dx <= 0 && dz <= 0) {
            this.drawEdge(shape, dx, y, dz, w, h, true);
            this.drawEdge(shape, dx, y, dz, w, h, false);
            return;
        }
        if (dx >= resolution - 1 && dz >= resolution - 1) {
            this.drawEdge(shape, dx + 1, y, dz, w, h, true);
            this.drawEdge(shape, dx, y, dz + 1, w, h, false);
            return;
        }
        if (dx <= 0 && dz >= resolution - 1) {
            this.drawEdge(shape, dx, y, dz, w, h, true);
            this.drawEdge(shape, dx, y, dz + 1, w, h, false);
            return;
        }
        if (dz <= 0 && dx >= resolution - 1) {
            this.drawEdge(shape, dx, y, dz, w, h, false);
            this.drawEdge(shape, dx + 1, y, dz, w, h, true);
            this.drawFace(shape, tile, dx, y, dz, dx - 1, dz, w, h, unit);
            return;
        }
        if (dx <= 0) {
            this.drawEdge(shape, dx, y, dz, w, h, true);
            this.drawFace(shape, tile, dx, y, dz, dx, dz - 1, w, h, unit);
            return;
        }
        if (dz <= 0) {
            this.drawEdge(shape, dx, y, dz, w, h, false);
            this.drawFace(shape, tile, dx, y, dz, dx - 1, dz, w, h, unit);
            return;
        }
        if (dx >= resolution - 1) {
            this.drawEdge(shape, dx + 1, y, dz, w, h, true);
            this.drawFace(shape, tile, dx, y, dz, dx, dz - 1, w, h, unit);
            this.drawFace(shape, tile, dx, y, dz, dx - 1, dz, w, h, unit);
            return;
        }
        if (dz >= resolution - 1) {
            this.drawEdge(shape, dx, y, dz + 1, w, h, false);
            this.drawFace(shape, tile, dx, y, dz, dx - 1, dz, w, h, unit);
            this.drawFace(shape, tile, dx, y, dz, dx, dz - 1, w, h, unit);
            return;
        }
        this.drawFace(shape, tile, dx, y, dz, dx - 1, dz, w, h, unit);
        this.drawFace(shape, tile, dx, y, dz, dx, dz - 1, w, h, unit);
    }
    
    private void drawFace(final RenderBuffer shape, final Tile tile, final int px, final int py, final int pz, final int dx, final int dz, final float w, final float h, final float unit) {
        final Cell cell = tile.getCell(dx, dz);
        if (cell == null) {
            return;
        }
        final float height = cell.value * this.settings.levels.worldHeight;
        final int y = this.getY(height, unit);
        if (y == py) {
            return;
        }
        if (dx != px) {
            shape.vertex(px * w, pz * h, (float)py);
            shape.vertex(px * w, (pz + 1) * h, (float)py);
            shape.vertex(px * w, (pz + 1) * h, (float)y);
            shape.vertex(px * w, pz * h, (float)y);
        }
        else {
            shape.vertex(px * w, pz * h, (float)py);
            shape.vertex((px + 1) * w, pz * h, (float)py);
            shape.vertex((px + 1) * w, pz * h, (float)y);
            shape.vertex(px * w, pz * h, (float)y);
        }
    }
    
    private void drawEdge(final RenderBuffer shape, final int px, final int py, final int pz, final float w, final float h, final boolean x) {
        final int y = 0;
        if (x) {
            shape.vertex(px * w, pz * h, (float)py);
            shape.vertex(px * w, (pz + 1) * h, (float)py);
            shape.vertex(px * w, (pz + 1) * h, (float)y);
            shape.vertex(px * w, pz * h, (float)y);
        }
        else {
            shape.vertex(px * w, pz * h, (float)py);
            shape.vertex((px + 1) * w, pz * h, (float)py);
            shape.vertex((px + 1) * w, pz * h, (float)y);
            shape.vertex(px * w, pz * h, (float)y);
        }
    }
    
    private int getY(final float height, final float unit) {
        if (height <= -this.settings.levels.waterLevel) {
            return (int)(this.settings.levels.waterLevel * unit);
        }
        return (int)((int)height * unit);
    }
}
