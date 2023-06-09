// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util;

import com.terraforged.engine.util.pos.PosIterator;
import com.terraforged.noise.util.NoiseUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.function.IntFunction;

public class RollingGrid<T>
{
    private final int size;
    private final int half;
    private final T[] grid;
    private final Generator<T> generator;
    private int startX;
    private int startZ;
    
    public RollingGrid(final int size, final IntFunction<T[]> constructor, final Generator<T> generator) {
        this.startX = 0;
        this.startZ = 0;
        this.size = size;
        this.half = size / 2;
        this.generator = generator;
        this.grid = constructor.apply(size * size);
    }
    
    public Iterable<T> getIterator() {
        return Arrays.asList(this.grid);
    }
    
    public PosIterator iterator() {
        return PosIterator.area(this.startX, this.startZ, this.size, this.size);
    }
    
    public int getStartX() {
        return this.startX;
    }
    
    public int getStartZ() {
        return this.startZ;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void setCenter(final int x, final int z) {
        this.setCenter(x - this.half, z - this.half, true);
    }
    
    public void setCenter(final int x, final int z, final boolean update) {
        this.setPos(x - this.half, z - this.half, update);
    }
    
    public void setPos(final int x, final int z) {
        this.setPos(x, z, true);
    }
    
    public void setPos(final int x, final int z, final boolean update) {
        if (update) {
            final int deltaX = x - this.startX;
            final int deltaZ = z - this.startZ;
            this.move(deltaX, deltaZ);
        }
        else {
            this.startX = x;
            this.startZ = z;
        }
    }
    
    public void move(final int x, final int z) {
        if (x != 0) {
            final int minX = (x < 0) ? (this.startX + x) : (this.startX + this.size - x + 1);
            for (int maxX = minX + Math.abs(x), px = minX; px < maxX; ++px) {
                final int dx = this.wrap(px);
                for (int dz = 0; dz < this.size; ++dz) {
                    final int index = this.index(dx, this.wrap(this.startZ + dz));
                    this.grid[index] = this.generator.generate(px, this.startZ + dz);
                }
            }
        }
        if (z != 0) {
            final int minZ = (z < 0) ? (this.startZ + z) : (this.startZ + this.size - z + 1);
            for (int maxZ = minZ + Math.abs(z), pz = minZ; pz < maxZ; ++pz) {
                final int dz2 = this.wrap(pz);
                for (int dx2 = 0; dx2 < this.size; ++dx2) {
                    final int index = this.index(this.wrap(this.startX + dx2), dz2);
                    this.grid[index] = this.generator.generate(this.startX + dx2, pz);
                }
            }
        }
        this.startX += x;
        this.startZ += z;
    }
    
    public T get(int x, int z) {
        x += this.startX;
        z += this.startZ;
        final int mx = this.wrap(x);
        final int mz = this.wrap(z);
        return this.grid[this.index(mx, mz)];
    }
    
    public void set(int x, int z, final T value) {
        x += this.startX;
        z += this.startZ;
        final int mx = this.wrap(x);
        final int mz = this.wrap(z);
        this.grid[this.index(mx, mz)] = value;
    }
    
    private int index(final int x, final int z) {
        return z * this.size + x;
    }
    
    private int wrap(final int value) {
        return (value % this.size + this.size) % this.size;
    }
    
    public static void main(final String[] args) {
        final RollingGrid<Chunk> grid = createGrid(32);
        final JLabel label = new JLabel();
        label.setIcon(new ImageIcon(render(0, 0, grid)));
        label.setFocusable(true);
        label.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'w': {
                        grid.move(0, -1);
                        label.setIcon(new ImageIcon(render(0, 0, grid)));
                        label.repaint();
                        break;
                    }
                    case 'a': {
                        grid.move(-1, 0);
                        label.setIcon(new ImageIcon(render(0, 0, grid)));
                        label.repaint();
                        break;
                    }
                    case 's': {
                        grid.move(0, 1);
                        label.setIcon(new ImageIcon(render(0, 0, grid)));
                        label.repaint();
                        break;
                    }
                    case 'd': {
                        grid.move(1, 0);
                        label.setIcon(new ImageIcon(render(0, 0, grid)));
                        label.repaint();
                        break;
                    }
                }
            }
        });
        final JFrame frame = new JFrame();
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }
    
    private static RollingGrid<Chunk> createGrid(final int size) {
        final RollingGrid<Chunk> grid = new RollingGrid<Chunk>(size, Chunk[]::new, Chunk::new);
        final PosIterator iterator = PosIterator.area(0, 0, size, size);
        while (iterator.next()) {
            final int x = iterator.x();
            final int z = iterator.z();
            grid.set(x, z, new Chunk(x, z));
        }
        return grid;
    }
    
    private static BufferedImage render(final int x, final int z, final RollingGrid<Chunk> grid) {
        final int size = grid.size << 4;
        final BufferedImage image = new BufferedImage(size, size, 1);
        final PosIterator chunkIterator = PosIterator.area(0, 0, grid.size, grid.size);
        while (chunkIterator.next()) {
            final int chunkX = x + chunkIterator.x();
            final int chunkZ = z + chunkIterator.z();
            final Chunk chunk = grid.get(chunkX, chunkZ);
            if (chunk == null) {
                continue;
            }
            final PosIterator pixel = PosIterator.area(chunkIterator.x() << 4, chunkIterator.z() << 4, 16, 16);
            while (pixel.next()) {
                image.setRGB(pixel.x(), pixel.z(), chunk.color.getRGB());
            }
        }
        return image;
    }
    
    private static class Chunk
    {
        private final Color color;
        
        public Chunk() {
            this.color = Color.BLACK;
        }
        
        public Chunk(final int x, final int z) {
            this.color = new Color(NoiseUtil.hash(x, z));
        }
    }
    
    public interface Generator<T>
    {
        T generate(final int p0, final int p1);
    }
}
