//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class N2DUtil {
    public N2DUtil() {
    }

    public static BufferedImage render(int width, int height, N2DUtil.PosVisitor<BufferedImage> visitor) {
        BufferedImage image = new BufferedImage(width, height, 1);
        iterate(width, height, image, visitor);
        return image;
    }

    public static JFrame display(int width, int height, N2DUtil.PixelShader<BufferedImage> shader) {
        return display(width, height, (x, z, ctx) -> {
            int rgb = shader.shade(x, z, ctx);
            ctx.setRGB(x, z, rgb);
        });
    }

    public static JFrame display(final int width, final int height, final N2DUtil.PosVisitor<BufferedImage> visitor) {
        final BufferedImage image = new BufferedImage(width, height, 1);
        JLabel label = new JLabel(new ImageIcon(image)) {
            public void paint(Graphics g) {
                N2DUtil.iterate(width, height, image, visitor);
                super.paint(g);
            }
        };
        JFrame frame = new JFrame();
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(3);
        return frame;
    }

    public static <T> void iterate(int width, int height, T ctx, N2DUtil.PosVisitor<T> visitor) {
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                visitor.visit(x, y, ctx);
            }
        }
    }

    public interface PixelShader<T> {
        int shade(int var1, int var2, T var3);
    }

    public interface PosVisitor<T> {
        void visit(int var1, int var2, T var3);
    }
}
