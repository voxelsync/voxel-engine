/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.paper.runtime.material;

import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.api.common.VoxRenderType;
import sync.voxel.engine.api.material.VoxMaterial;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoxelMaterial implements VoxMaterial {

    private final Material vaMaterial;
    private final VoxKey key;
    private final VoxRenderType renderType;
    private final Map<String, Object> nbt = new HashMap<>();

    public static Set<VoxMaterial> values() {
        return materials;
    }

    public static VoxMaterial valueOf(String nameSpace, String identifier) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(nameSpace + ":" + identifier))
                .findFirst()
                .orElse(null);
    }

    public static VoxMaterial valueOf(String s) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(s))
                .findFirst()
                .orElse(null);
    }

    public static VoxMaterial valueOf(VoxKey key) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }

    public static VoxMaterial valueOf(@NotNull Material vaMaterial) {
        return valueOf(vaMaterial.getKey().toString());
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull VoxelMaterial forkMaterial(Material vaMaterial, VoxKey key, VoxRenderType renderType) {
        return new VoxelMaterial(vaMaterial, key, renderType);
    }


    public VoxelMaterial(Material vaMaterial, VoxKey key, VoxRenderType renderType) {
        this.renderType = renderType;
        this.vaMaterial = vaMaterial;
        this.key = key;
        materials.add(this);
    }

    @Override
    public VoxKey getKey() {
        return key;
    }

    @Override
    public Material getVaMaterial() {
        return vaMaterial;
    }

    @Override
    public VoxRenderType getRenderType() {
        return renderType;
    }

    @Override
    public <T> T getAttribute(String key, Class<T> type, T defaultValue) {
        return get("attribute", key, type, defaultValue);
    }

    /**
     * Gets a value from NBT storage using a namespaced key (format "namespace:key")
     */
    public <T> T get(String namespacedKey, @NotNull Class<T> type, T defaultValue) {
        Object value = nbt.get(namespacedKey);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return defaultValue;
    }

    /**
     * Stores a value in NBT storage using a namespaced key (format "namespace:key")
     * @return the previous value associated with the key, or null if there was none
     */
    public <T> T store(String namespacedKey, @NotNull Class<T> type, T value) {
        Object oldValue = nbt.put(namespacedKey, value);
        if (type.isInstance(oldValue)) {
            return type.cast(oldValue);
        }
        return null;
    }

    /**
     * Alternative get method that separates namespace and key
     */
    public <T> T get(String namespace, String key, Class<T> type, T defaultValue) {
        return get(namespace + ":" + key, type, defaultValue);
    }

    /**
     * Alternative store method that separates namespace and key
     */
    public <T> T store(String namespace, String key, Class<T> type, T value) {
        return store(namespace + ":" + key, type, value);
    }


}
