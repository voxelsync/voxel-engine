/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api.runtime.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sync.voxel.api.VoxelEngine;
import sync.voxel.api.startup.material.VoMaterial;

/**
 * Represents a Voxel item stack with extended functionality including persistent data storage.
 * This interface provides methods for creating and manipulating item stacks with custom metadata.
 *
 * <p>All items created through this interface automatically store:
 * <ul>
 *   <li>A unique UUID</li>
 *   <li>Creation timestamp</li>
 *   <li>Creation reason</li>
 *   <li>Material identifier</li>
 * </ul>
 * </p>
 */
public interface VoItemStack {

    /**
     * Creates a new Voxel item stack with default amount (1) and CUSTOM creation reason.
     *
     * @param material the Bukkit material to create the item from
     * @return a new Voxel item stack
     */
    static @NotNull VoItemStack of(Material material) {
        return of(VoMaterial.valueOf(material), 1, VoCreateReason.CUSTOM);
    }

    /**
     * Creates a new Voxel item stack with default amount (1) and specified creation reason.
     *
     * @param material the Bukkit material to create the item from
     * @param reason the reason for item creation
     * @return a new Voxel item stack
     */
    static @NotNull VoItemStack of(Material material, VoCreateReason reason) {
        return of(VoMaterial.valueOf(material), 1, reason);
    }

    /**
     * Creates a new Voxel item stack with default amount (1) and CUSTOM creation reason.
     *
     * @param voMaterial the Voxel material to create the item from
     * @return a new Voxel item stack
     */
    @Contract(pure = true)
    static @NotNull VoItemStack of(VoMaterial voMaterial) {
        return of(voMaterial, 1, VoCreateReason.CUSTOM);
    }

    /**
     * Creates a new Voxel item stack with default amount (1) and specified creation reason.
     *
     * @param voMaterial the Voxel material to create the item from
     * @param reason the reason for item creation
     * @return a new Voxel item stack
     */
    @Contract(pure = true)
    static @NotNull VoItemStack of(VoMaterial voMaterial, VoCreateReason reason) {
        return of(voMaterial, 1, reason);
    }

    /**
     * Creates a new Voxel item stack with specified amount and CUSTOM creation reason.
     *
     * @param material the Bukkit material to create the item from
     * @param amount the stack amount
     * @return a new Voxel item stack
     */
    static @NotNull VoItemStack of(Material material, int amount) {
        return of(VoMaterial.valueOf(material), amount, VoCreateReason.CUSTOM);
    }

    /**
     * Creates a new Voxel item stack with specified amount and creation reason.
     *
     * @param material the Bukkit material to create the item from
     * @param amount the stack amount
     * @param reason the reason for item creation
     * @return a new Voxel item stack
     */
    static @NotNull VoItemStack of(Material material, int amount, VoCreateReason reason) {
        return of(VoMaterial.valueOf(material), amount, reason);
    }

    /**
     * Creates a new Voxel item stack with specified amount and CUSTOM creation reason.
     *
     * @param voMaterial the Voxel material to create the item from
     * @param amount the stack amount
     * @return a new Voxel item stack
     */
    @Contract(pure = true)
    static @NotNull VoItemStack of(@NotNull VoMaterial voMaterial, int amount) {
        return of(voMaterial, amount, VoCreateReason.CUSTOM);
    }

    /**
     * Creates a new Voxel item stack with specified amount and creation reason.
     *
     * @param voMaterial the Voxel material to create the item from
     * @param amount the stack amount
     * @param reason the reason for item creation
     * @return a new Voxel item stack
     */
    static VoItemStack of(@NotNull VoMaterial voMaterial, int amount, VoCreateReason reason) {
        if (amount <= 0) throw new RuntimeException("An ItemStack can not have an amount 0 or lower");
        return VoxelEngine.registerVoItemStack(voMaterial, amount, reason);
    }

    /**
     * Creates a new Voxel item stack with specified amount and creation reason.
     *
     * @param stack vaItemStack for
     * @param reason the reason for item creation
     * @return a new Voxel item stack
     */
    static VoItemStack of(ItemStack stack, VoCreateReason reason) {
        return VoxelEngine.registerVoItemStack(stack, reason);
    }

    /**
     * Converts this Voxel item stack to a Bukkit ItemStack.
     *
     * @return the Bukkit ItemStack representation
     */
    ItemStack toItemStack();

    /**
     * Sets persistent data on the item using the default namespace.
     *
     * @param key the data key
     * @param type the data type
     * @param value the data value
     * @return this item stack for chaining
     */
    @NotNull
    VoItemStack setPersistentData(@NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value);


    /**
     * Removes persistent data from the item using the default namespace.
     *
     * @param key the data key to remove
     * @return this item stack for chaining
     */
    @NotNull
    VoItemStack removePersistentData(@NotNull String key);

    /**
     * Gets persistent data from the item using the default namespace.
     *
     * @param <T> the return type
     * @param key the data key
     * @param type the data type
     * @return the stored value or null if not found
     */
    @Nullable
    <T> T getPersistentData(@NotNull String key, @NotNull PersistentDataType<?, T> type);

    /**
     * Checks if persistent data exists using the default namespace.
     *
     * @param key the data key to check
     * @return true if the data exists
     */
    boolean hasPersistentData(@NotNull String key);

    /**
     * Sets a String value in persistent data using the default namespace.
     *
     * @param key the data key
     * @param value the String value
     * @return this item stack for chaining
     */
    @NotNull
    VoItemStack setString(@NotNull String key, @NotNull String value);

    /**
     * Gets a String value from persistent data using the default namespace.
     *
     * @param key the data key
     * @return the stored String or null if not found
     */
    @Nullable
    String getString(@NotNull String key);

    /**
     * Sets an integer value in persistent data using the default namespace.
     *
     * @param key the data key
     * @param value the integer value
     * @return this item stack for chaining
     */
    @NotNull
    VoItemStack setInt(@NotNull String key, int value);

    /**
     * Gets an integer value from persistent data using the default namespace.
     *
     * @param key the data key
     * @return the stored integer or null if not found
     */
    @Nullable
    Integer getInt(@NotNull String key);

    /**
     * Sets a double value in persistent data using the default namespace.
     *
     * @param key the data key
     * @param value the double value
     * @return this item stack for chaining
     */
    @NotNull
    VoItemStack setDouble(@NotNull String key, double value);

    /**
     * Gets a double value from persistent data using the default namespace.
     *
     * @param key the data key
     * @return the stored double or null if not found
     */
    @Nullable
    Double getDouble(@NotNull String key);

    /**
     * Sets a long value in persistent data using the default namespace.
     *
     * @param key the data key
     * @param value the long value
     * @return this item stack for chaining
     */
    @NotNull
    VoItemStack setLong(@NotNull String key, long value);

    /**
     * Gets a long value from persistent data using the default namespace.
     *
     * @param key the data key
     * @return the stored long or null if not found
     */
    @Nullable
    Long getLong(@NotNull String key);

    /**
     * Sets a float value in persistent data using the default namespace.
     *
     * @param key the data key
     * @param value the float value
     * @return this item stack for chaining
     */
    @NotNull
    VoItemStack setFloat(@NotNull String key, float value);

    /**
     * Gets a float value from persistent data using the default namespace.
     *
     * @param key the data key
     * @return the stored float or null if not found
     */
    @Nullable
    Float getFloat(@NotNull String key);

    /**
     * Sets a boolean value in persistent data using the default namespace.
     *
     * @param key the data key
     * @param value the boolean value
     * @return this item stack for chaining
     */
    @NotNull
    VoItemStack setBoolean(@NotNull String key, boolean value);

    /**
     * Gets a boolean value from persistent data using the default namespace.
     *
     * @param key the data key
     * @return the stored boolean (false if not found)
     */
    boolean getBoolean(@NotNull String key);
}