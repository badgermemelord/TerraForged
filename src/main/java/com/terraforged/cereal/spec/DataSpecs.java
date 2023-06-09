// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.spec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DataSpecs
{
    private static final Map<String, DataSpec<?>> specs;
    private static final Map<Class<?>, SubSpec<?>> subSpecs;
    private static final Map<Class<?>, SubSpec<?>> subSpecLookup;
    
    public static void register(final DataSpec<?> spec) {
        DataSpecs.specs.put(spec.getName(), spec);
    }
    
    public static <T, V extends T> void registerSub(final Class<T> type, final DataSpec<V> subSpec) {
        final SubSpec<T> spec = (SubSpec<T>)DataSpecs.subSpecs.computeIfAbsent(type, (Function<? super Class<?>, ? extends SubSpec<?>>)SubSpec::new);
        spec.register(subSpec.getType(), subSpec);
        DataSpecs.subSpecLookup.put(subSpec.getType(), spec);
    }
    
    public static boolean hasSpec(final String name) {
        return DataSpecs.specs.containsKey(name);
    }
    
    public static boolean isSubSpec(final Object instance) {
        return DataSpecs.subSpecLookup.containsKey(instance.getClass());
    }
    
    public static DataSpec<?> getSpec(final String name) {
        final DataSpec<?> spec = DataSpecs.specs.get(name);
        if (spec == null) {
            throw new NullPointerException("Missing spec: '" + name + '\'');
        }
        return spec;
    }
    
    public static SubSpec<?> getSubSpec(final Class<?> type) {
        return DataSpecs.subSpecs.get(type);
    }
    
    public static SubSpec<?> getSubSpec(final Object instance) {
        return DataSpecs.subSpecLookup.get(instance.getClass());
    }
    
    public static <T> List<DataSpec<?>> getSpecs(final Class<T> type) {
        final List<DataSpec<?>> all = new ArrayList<DataSpec<?>>(DataSpecs.specs.values());
        all.sort(Comparator.comparing((Function<? super DataSpec<?>, ? extends Comparable>)DataSpec::getName));
        final List<DataSpec<?>> list = new ArrayList<DataSpec<?>>(all.size());
        for (final DataSpec<?> spec : all) {
            if (type.isAssignableFrom(spec.getType())) {
                list.add(spec);
            }
        }
        return list;
    }
    
    static {
        specs = new ConcurrentHashMap<String, DataSpec<?>>();
        subSpecs = new ConcurrentHashMap<Class<?>, SubSpec<?>>();
        subSpecLookup = new ConcurrentHashMap<Class<?>, SubSpec<?>>();
    }
}
