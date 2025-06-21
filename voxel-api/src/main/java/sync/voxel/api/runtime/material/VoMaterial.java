/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api.runtime.material;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.leycm.storage.StorageSection;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a material (material/block) in the VoxelSync system.
 * <p>
 * This interface defines the core properties of a Voxel material including its namespace,
 * identifier, corresponding vanilla Minecraft material, settings, and rarity.
 * </p>
 *
 * <p>
 * The interface also maintains a registry of all VoMaterial instances and provides utility
 * methods to access them.
 * </p>
 *
 * @see VoRarity
 * @see org.leycm.storage.StorageSection
 */
public interface VoMaterial {
    Set<VoMaterial> materials = new HashSet<>();

    /**
     * Gets the namespace of this material.
     * <p>
     * The namespace typically identifies the plugin or mod that added this material.
     * </p>
     *
     * @return The namespace of this material
     */
    String nameSpace();

    /**
     * Gets the unique identifier of this material within its namespace.
     *
     * @return The material's identifier
     */
    String identifier();

    /**
     * Gets the corresponding vanilla Minecraft material for this Voxel material.
     *
     * @return The vanilla Material equivalent
     */
    Material vaMaterial();

    /**
     * Gets the settings configuration for this material.
     *
     * @return The StorageSection containing this material's settings
     */
    StorageSection settings();

    /**
     * Gets the rarity of this material.
     *
     * @return The VoRarity of this material
     */
    VoRarity rarity();

    /**
     * Checks if this material is a 3d material.
     *
     * @return true if this material is a 3d, false otherwise
     */
    boolean has3dModel();

    /**
     * Checks if this material is an air block.
     *
     * @return true if this material is air (AIR, CAVE_AIR, or VOID_AIR), false otherwise
     */
    boolean isAir();

    /**
     * Checks if this material is a block (can be placed in the world).
     *
     * @return true if this material is a placeable block, false otherwise
     */
    boolean isBlock();

    /**
     * Checks if this material is a solid block.
     * Solid blocks can be stood on and block movement.
     *
     * @return true if this material is a solid block, false otherwise
     */
    boolean isSolidBlock();

    /**
     * Checks if this material is a transparent block (not fully opaque).
     *
     * @return true if this material is transparent, false otherwise
     */
    boolean isOccluding();

    /**
     * Checks if this material is edible.
     *
     * @return true if this material can be eaten, false otherwise
     */
    boolean isEdible();

    /**
     * Checks if this material is flammable.
     *
     * @return true if this material is flammable, false otherwise
     */
    boolean isFlammable();

    /**
     * Checks if this material can burn.
     *
     * @return true if this material can burn, false otherwise
     */
    boolean isBurnable();

    /**
     * Checks if this material can be used as fuel.
     *
     * @return true if this material can be used as fuel, false otherwise
     */
    boolean isFuel();

    /**
     * Checks if this material can be interacted with.
     * @deprecated by Bukkit
     * @return true if this material is interactable, false otherwise
     */
    @Deprecated
    boolean isInteractable();

    /**
     * Checks if this material is a music disc.
     *
     * @return true if this material is a record, false otherwise
     */
    boolean isRecord();

    /**
     * Checks if this material is affected by gravity.
     *
     * @return true if this material has gravity, false otherwise
     */
    boolean hasGravity();

    /**
     * Gets the hardness of this material.
     *
     * @return the hardness value
     */
    float getHardness();

    /**
     * Gets the blast resistance of this material.
     *
     * @return the blast resistance value
     */
    float getBlastResistance();

    /**
     * Gets the maximum stack size for this material.
     *
     * @return the maximum stack size
     */
    int getMaxStackSize();

    /**
     * Gets the maximum durability of this material.
     *
     * @return the maximum durability
     */
    short getMaxDurability();

    /**
     * Checks if this material is an material.
     *
     * @return true if this material is an material, false otherwise
     */
    boolean isItem();

    /**
     * Checks if this material is a legacy material.
     *
     * @return true if this material is legacy, false otherwise
     */
    boolean isLegacy();

    /**
     * Gets the key of this material.
     *
     * @return the material key
     */
    String getKey();

    /**
     * Gets the name of this material.
     *
     * @return the material name
     */
    String name();

    /**
     * Gets the ordinal of this material.
     *
     * @return the material ordinal
     */
    int ordinal();

    /**
     * Gets the string representation of this material.
     *
     * @return the string representation
     */
    String toString();

    /**
     * Checks if this material is collidable.
     *
     * @return true if this material is collidable, false otherwise
     */
    boolean isCollidable();

    /**
     * Gets the slipperiness of this material.
     *
     * @return the slipperiness value
     */
    float getSlipperiness();

    /**
     * Checks if this material is solid.
     *
     * @return true if this material is solid, false otherwise
     */
    boolean isSolid();

    /**
     * Gets a specific setting value from this material's configuration.
     *
     * @param key The setting key to retrieve
     * @param type The class type of the setting value
     * @param defaultValue The default value to return if not found
     * @param <T> The type of the setting value
     * @return The setting value or defaultValue if not found
     */
    <T> T getSetting(String key, Class<T> type, T defaultValue);

    /**
     * Gets all registered VoMaterial instances.
     *
     * @return An unmodifiable Set of all registered materials
     */
    static Set<VoMaterial> values() {
        return materials;
    }

    /**
     * Finds a VoMaterial by its namespace and identifier.
     *
     * @param nameSpace The namespace to search for
     * @param identifier The identifier to search for
     * @return The matching VoMaterial, or null if not found
     */
    static VoMaterial valueOf(String nameSpace, String identifier) {
        return materials.stream()
                .filter(m -> m.nameSpace().equals(nameSpace) && m.identifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds a VoMaterial by its namespace:identifier.
     *
     * @param s The namespace and identifier to search for minecraft:air
     * @return The matching VoMaterial, or null if not found
     */
    static VoMaterial valueOf(String s) {
        return materials.stream()
                .filter(m -> (m.nameSpace() + ":" + m.identifier()).equals(s))
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
    static VoMaterial valueOf(@NotNull Material vaMaterial) {
        return valueOf(vaMaterial.getKey().toString());
    }

}