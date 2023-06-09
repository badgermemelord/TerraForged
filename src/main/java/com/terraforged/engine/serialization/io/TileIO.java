// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.io;

import com.terraforged.engine.concurrent.Disposable;
import com.terraforged.engine.tile.Tile;
import com.terraforged.engine.tile.gen.TileResources;
import com.terraforged.engine.util.pos.PosUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TileIO
{
    public static DataInputStream getInput(final Path dir, final int x, final int z) throws IOException {
        if (!Files.exists(dir, new LinkOption[0])) {
            Files.createDirectories(dir, (FileAttribute<?>[])new FileAttribute[0]);
        }
        final Path path = dir.resolve(getFileName(x, z));
        return new DataInputStream(new GZIPInputStream(new BufferedInputStream(Files.newInputStream(path, new OpenOption[0]))));
    }
    
    public static DataOutputStream getOutput(final Path dir, final Tile tile) throws IOException {
        if (!Files.exists(dir, new LinkOption[0])) {
            Files.createDirectories(dir, (FileAttribute<?>[])new FileAttribute[0]);
        }
        final Path path = dir.resolve(getFileName(tile));
        return new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(Files.newOutputStream(path, new OpenOption[0]))));
    }
    
    public static String getFileName(final Tile tile) {
        return getFileName(tile.getRegionX(), tile.getRegionZ());
    }
    
    public static String getFileName(final int rx, final int rz) {
        return PosUtil.pack(rx, rz) + ".tile";
    }
    
    public static void writeTo(final Tile tile, final DataOutput out) throws IOException {
        out.writeInt(tile.getRegionX());
        out.writeInt(tile.getRegionZ());
        out.writeInt(tile.getGenerationSize());
        writeCells(tile, out);
    }
    
    public static Tile readFrom(final DataInput in, final TileResources resources, final Disposable.Listener<Tile> listener) throws IOException {
        final int x = in.readInt();
        final int z = in.readInt();
        final int size = in.readInt();
        final Tile tile = new Tile(x, z, size, 0, resources, listener);
        readCells(in, tile);
        return tile;
    }
    
    private static void writeCells(final Tile tile, final DataOutput out) throws IOException {
        try {
            tile.iterate((cell, dx, dz) -> {
                try {
                    CellIO.writeTo(cell, out);
                }
                catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            });
        }
        catch (RuntimeIOException e2) {
            throw e2.getCause();
        }
    }
    
    private static void readCells(final DataInput in, final Tile tile) throws IOException {
        try {
            tile.generate((cell, dx, dz) -> {
                try {
                    CellIO.readTo(in, cell);
                }
                catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            });
        }
        catch (RuntimeIOException e2) {
            throw e2.getCause();
        }
    }
}
