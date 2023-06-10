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
public @interface LegacyInt {
    Function<Field, Integer> GETTER = field -> {
        LegacyInt legacy = (LegacyInt)field.getAnnotation(LegacyInt.class);
        return legacy == null ? null : legacy.value();
    };

    int value();
}
