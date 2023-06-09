// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.io;

import java.io.IOException;

public class RuntimeIOException extends RuntimeException
{
    private final IOException cause;
    
    public RuntimeIOException(final IOException cause) {
        this.cause = cause;
    }
    
    @Override
    public IOException getCause() {
        return this.cause;
    }
}
