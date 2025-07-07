/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.api.material;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.api.common.VoxRenderType;

import java.util.HashSet;
import java.util.Set;

public interface VoxMaterial {
    Set<VoxMaterial> materials = new HashSet<>();

    // ===== ===== STATIC_MATERIAL_CONTAINER ===== =====

    /**
     * Gets all registered VoMaterial instances.
     *
     * @return An unmodifiable Set of all registered materials
     */
    static Set<VoxMaterial> values() {
        return materials;
    }

    /**
     * Finds a VoMaterial by its namespace and identifier.
     *
     * @param nameSpace The namespace to search for
     * @param identifier The identifier to search for
     * @return The matching VoMaterial, or null if not found
     */
    static VoxMaterial valueOf(String nameSpace, String identifier) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(nameSpace + ":" + identifier))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a VoMaterial by its namespace:identifier.
     *
     * @param s The namespace and identifier to search for minecraft:air
     * @return The matching VoMaterial, or null if not found
     */
    static VoxMaterial valueOf(String s) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(s))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a VoMaterial by its namespace:identifier.
     *
     * @param key The key containing namespace and identifier, to search for minecraft:air
     * @return The matching VoMaterial, or null if not found
     */
    static VoxMaterial valueOf(VoxKey key) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Converts a Bukkit Material to its corresponding VoMaterial.
     *
     * @param vaMaterial Bukkit Material to convert (not null)
     * @return Matching VoMaterial or null if not found
     * @throws IllegalArgumentException if vaMaterial is null
     */
    static VoxMaterial valueOf(@NotNull Material vaMaterial) {
        return valueOf(vaMaterial.getKey().toString());
    }

    // ===== ===== MATERIAL_INSTANCE_CLASS ===== =====


    // TODO : add docs
    VoxKey getKey();

    // TODO : add docs
    Material getVaMaterial();

    // TODO : add docs
    VoxRenderType getRenderType();

    /**
     * Gets a specific setting value from this material's configuration.
     *
     * @param key The setting key to retrieve
     * @param type The class type of the setting value
     * @param defaultValue The default value to return if not found
     * @param <T> The type of the setting value
     * @return The setting value or defaultValue if not found
     */
    <T> T getAttribute(String key, Class<T> type, T defaultValue);

}
