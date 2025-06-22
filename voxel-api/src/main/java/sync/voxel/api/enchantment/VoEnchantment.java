/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api.enchantment;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import sync.voxel.api.common.VoKey;

import java.util.HashSet;
import java.util.Set;

public interface VoEnchantment {
    Set<VoEnchantment> enchantments = new HashSet<>();

    // ===== ===== STATIC_ENCHANTMENT_CONTAINER ===== =====

    /**
     * Gets all registered VoEnchantment instances.
     *
     * @return An unmodifiable Set of all registered materials
     */
    static Set<VoEnchantment> values() {
        return enchantments;
    }

    /**
     * Finds a VoEnchantment by its namespace and identifier.
     *
     * @param nameSpace The namespace to search for
     * @param identifier The identifier to search for
     * @return The matching VoEnchantment, or null if not found
     */
    static VoEnchantment valueOf(String nameSpace, String identifier) {
        return enchantments.stream()
                .filter(m -> m.getKey().toString().equals(nameSpace + ":" + identifier))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a VoEnchantment by its namespace:identifier.
     *
     * @param s The namespace and identifier to search for minecraft:air
     * @return The matching VoEnchantment, or null if not found
     */
    static VoEnchantment valueOf(String s) {
        return enchantments.stream()
                .filter(m -> m.getKey().toString().equals(s))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a VoEnchantment by its namespace:identifier.
     *
     * @param key The key containing namespace and identifier, to search for minecraft:air
     * @return The matching VoEnchantment, or null if not found
     */
    static VoEnchantment valueOf(VoKey key) {
        return enchantments.stream()
                .filter(m -> m.getKey().toString().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Converts a Bukkit Enchantment to its corresponding VoEnchantment.
     *
     * @return Matching VoEnchantment or null if not found
     * @throws IllegalArgumentException if vaEnchantment is null
     */
    static VoEnchantment valueOf(@NotNull Enchantment vaEnchantment) {
        return valueOf(vaEnchantment.getKey().toString());
    }

    // ===== ===== ENCHANTMENT_INSTANCE_CLASS ===== =====


    // TODO : add docs
    VoKey getKey();
    
    // TODO : add docs
    int getTextureIdentifier();


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
