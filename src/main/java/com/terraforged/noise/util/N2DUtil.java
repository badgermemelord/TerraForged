// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class N2DUtil
{
    public static BufferedImage render(final int width, final int height, final PosVisitor<BufferedImage> visitor) {
        final BufferedImage image = new BufferedImage(width, height, 1);
        iterate(width, height, image, visitor);
        return image;
    }
    
    public static JFrame display(final int width, final int height, final PixelShader<BufferedImage> shader) {
        final int rgb;
        return display(width, height, (x, z, ctx) -> {
            rgb = shader.shade(x, z, ctx);
            ctx.setRGB(x, z, rgb);
        });
    }
    
    public static JFrame display(final int width, final int height, final PosVisitor<BufferedImage> visitor) {
        final BufferedImage image = new BufferedImage(width, height, 1);
        final JLabel label = new JLabel(new ImageIcon(image)) {
            @Override
            public void paint(final Graphics g) {
                N2DUtil.iterate(width, height, image, visitor);
                super.paint(g);
            }
        };
        final JFrame frame = new JFrame();
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(3);
        return frame;
    }
    
    public static <T> void iterate(final int width, final int height, final T ctx, final PosVisitor<T> visitor) {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                visitor.visit(x, y, ctx);
            }
        }
    }
    
    public interface PixelShader<T>
    {
        int shade(final int p0, final int p1, final T p2);
    }
    
    public interface PosVisitor<T>
    {
        void visit(final int p0, final int p1, final T p2);
    }
}
