/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.terraforged.mod.mixin.common;

import com.terraforged.mod.registry.hooks.BuiltinHook;
import com.terraforged.mod.registry.hooks.DataLoadHook;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.resources.RegistryResourceAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RegistryAccess.class)
public class MixinRegistryAccess {
    @Final
    @Shadow
    private static RegistryAccess.RegistryHolder BUILTIN;

    @Inject(
            method = "builtin",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/resources/RegistryReadOps;createAndLoad(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resources/RegistryResourceAccess;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/resources/RegistryReadOps;"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void onBuiltin(CallbackInfoReturnable<RegistryAccess.RegistryHolder> cir,
                                  RegistryAccess.RegistryHolder holder,
                                  RegistryResourceAccess.InMemoryStorage storage) {
        BuiltinHook.injectBuiltin(BUILTIN, storage);
    }

    @Inject(method = "load", at = @At("RETURN"))
    private static void onLoad(RegistryAccess access, RegistryReadOps<?> ops, CallbackInfo ci) {
        DataLoadHook.loadData(access, ops);
    }
}
