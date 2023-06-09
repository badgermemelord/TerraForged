// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {
    String lower() default "";
    
    String upper() default "";
    
    float pad() default -1.0f;
}
