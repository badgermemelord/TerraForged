//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.serialization.annotation.legacy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.function.Function;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LegacyFloat {
    Function<Field, Float> GETTER = field -> {
        LegacyFloat legacy = (LegacyFloat)field.getAnnotation(LegacyFloat.class);
        return legacy == null ? null : legacy.value();
    };

    float value();
}