/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.paper.runtime.material;

import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sync.voxel.api.common.VoKey;
import sync.voxel.api.common.VoRenderType;
import sync.voxel.api.material.VoMaterial;

import java.util.HashMap;
import java.util.Map;

public class VoxelMaterial implements VoMaterial {

    private final Material vaMaterial;
    private final VoKey key;
    private final VoRenderType renderType;
    private final Map<String, Object> nbt = new HashMap<>();

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull VoxelMaterial forkMaterial(Material vaMaterial, VoKey key, VoRenderType renderType) {
        return new VoxelMaterial(vaMaterial, key, renderType);
    }


    public VoxelMaterial(Material vaMaterial, VoKey key, VoRenderType renderType) {
        this.renderType = renderType;
        this.vaMaterial = vaMaterial;
        this.key = key;
    }

    @Override
    public VoKey getKey() {
        return key;
    }

    @Override
    public Material getVaMaterial() {
        return vaMaterial;
    }

    @Override
    public int getTextureIdentifier() {
        return get("render:texture_identifier", Integer.class, 0);
    }

    @Override
    public VoRenderType getRenderType() {
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
