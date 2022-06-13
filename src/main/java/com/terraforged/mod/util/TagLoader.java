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

package com.terraforged.mod.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagManager;
import net.minecraft.util.profiling.InactiveProfiler;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TagLoader {
    private static final Executor DIRECT_EXECUTOR = Runnable::run;
    
    public static void bindTags(RegistryAccess access, ResourceManager resources) {
        var tagManager = new TagManager(access);
        
        tagManager.reload(
                TagLoader::complete,
                resources,
                InactiveProfiler.INSTANCE,
                InactiveProfiler.INSTANCE,
                DIRECT_EXECUTOR,
                DIRECT_EXECUTOR
        );

        var result = tagManager.getResult();
        for (var entry : result) {
            bind(entry, access);
        }
    }

    private static <T> void bind(TagManager.LoadResult<T> result, RegistryAccess access) {
        var registry = access.ownedRegistry(result.key());

        if (registry.isEmpty()) return;

//        registry.get().bindTags(result.tags().entrySet().stream().collect(Collectors.toMap(
//                e -> TagKey.create(result.key(), e.getKey()),
//                e -> e.getValue().getValues()
//        )));
    }

    @Nonnull
    private static <T> CompletableFuture<T> complete(T t) {
        return CompletableFuture.completedFuture(t);
    }
}
