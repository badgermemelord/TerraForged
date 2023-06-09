// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.concurrent.batch;

public class BatchTaskException extends RuntimeException
{
    public BatchTaskException(final String message) {
        super(message);
    }
    
    public BatchTaskException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
