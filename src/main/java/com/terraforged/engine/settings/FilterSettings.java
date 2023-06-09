// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.settings;

import com.terraforged.engine.serialization.annotation.Comment;
import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Serializable;

@Serializable
public class FilterSettings
{
    public Erosion erosion;
    public Smoothing smoothing;
    
    public FilterSettings() {
        this.erosion = new Erosion();
        this.smoothing = new Smoothing();
    }
    
    @Serializable
    public static class Erosion
    {
        @Range(min = 10.0f, max = 250.0f)
        @Comment({ "The average number of water droplets to simulate per chunk" })
        public int dropletsPerChunk;
        @Range(min = 1.0f, max = 32.0f)
        @Comment({ "Controls the number of iterations that a single water droplet is simulated for" })
        public int dropletLifetime;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "Controls the starting volume of water that a simulated water droplet carries" })
        public float dropletVolume;
        @Range(min = 0.1f, max = 1.0f)
        @Comment({ "Controls the starting velocity of the simulated water droplet" })
        public float dropletVelocity;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "Controls how quickly material dissolves (during erosion)" })
        public float erosionRate;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "Controls how quickly material is deposited (during erosion)" })
        public float depositeRate;
        
        public Erosion() {
            this.dropletsPerChunk = 135;
            this.dropletLifetime = 12;
            this.dropletVolume = 0.7f;
            this.dropletVelocity = 0.7f;
            this.erosionRate = 0.5f;
            this.depositeRate = 0.5f;
        }
        
        public Erosion copy() {
            final Erosion erosion = new Erosion();
            erosion.dropletsPerChunk = this.dropletsPerChunk;
            erosion.erosionRate = this.erosionRate;
            erosion.depositeRate = this.depositeRate;
            erosion.dropletLifetime = this.dropletLifetime;
            erosion.dropletVolume = this.dropletVolume;
            erosion.dropletVelocity = this.dropletVelocity;
            return erosion;
        }
    }
    
    @Serializable
    public static class Smoothing
    {
        @Range(min = 0.0f, max = 5.0f)
        @Comment({ "Controls the number of smoothing iterations" })
        public int iterations;
        @Range(min = 0.0f, max = 5.0f)
        @Comment({ "Controls the smoothing radius" })
        public float smoothingRadius;
        @Range(min = 0.0f, max = 1.0f)
        @Comment({ "Controls how strongly smoothing is applied" })
        public float smoothingRate;
        
        public Smoothing() {
            this.iterations = 1;
            this.smoothingRadius = 1.8f;
            this.smoothingRate = 0.9f;
        }
    }
}
