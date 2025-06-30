/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api.common;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import sync.voxel.api.VoxelEngine;
import sync.voxel.api.enchantment.VoxEnchantment;
import sync.voxel.api.material.VoxMaterial;

import java.util.List;

public interface VoxItem {

    @Contract("_ -> new")
    static @NotNull VoxItem edit(@NotNull ItemStack item) {
        return VoxelEngine.voxItemTunnel(item);
    }

    @Contract("_ -> new")
    static @NotNull VoxItem warp(@NotNull ItemStack item) {
        return VoxelEngine.voxItemTunnel(item);
    }

    @Contract("_ -> new")
    static @NotNull VoxItem clone(@NotNull ItemStack item) {
        return VoxelEngine.voxItemTunnel(item.clone());
    }

    ItemStack toNewItemStack();

    ItemStack stack();

    ItemMeta meta();

    // ===== PERSISTENT DATA CONTAINER METHODS =====
    @NotNull VoxItem setPersistentData(@NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value);

    @NotNull VoxItem setPersistentData(@NotNull String namespace, @NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value);

    @NotNull VoxItem removePersistentData(@NotNull String key);

    @NotNull VoxItem removePersistentData(@NotNull String namespace, @NotNull String key);

    @Nullable <T> T getPersistentData(@NotNull String key, @NotNull PersistentDataType<?, T> type);

    @Nullable <T> T getPersistentData(@NotNull String namespace, @NotNull String key, @NotNull PersistentDataType<?, T> type);

    boolean hasPersistentData(@NotNull String key);

    boolean hasPersistentData(@NotNull String namespace, @NotNull String key);

    // ===== VOXEL ENCHANTMENT METHODS =====
    VoxItem addEnchant(@NotNull VoxEnchantment voEnchant, @NotNull Integer level);

    VoxItem removeEnchant(@NotNull VoxEnchantment voEnchant);

    boolean hasEnchant(@NotNull VoxEnchantment enchant);

    boolean hasAnyEnchant();

    int getEnchantLevel(@NotNull VoxEnchantment enchant);

    // ===== VOXEL MATERIAL METHODS =====
    @SuppressWarnings("UnstableApiUsage")
    VoxItem setVoMaterial(@NotNull VoxMaterial voMaterial);

    @SuppressWarnings("UnstableApiUsage")
    VoxMaterial getVoMaterial();

    // ===== ITEM LORE METHODS =====
    List<Component> getLore();

    void setLore(List<Component> lore);

    Component getLoreLine(int index);

    VoxItem addLoreLine(Component line);

    VoxItem addLoreLine(int index, Component line);

    VoxItem setLoreLine(int index, Component line);

    VoxItem removeLoreLine(int index);

    VoxItem removeLastLoreLine();

    VoxItem removeLoreLine(Component line);

    // ===== ITEM NAME / DISPLAY_NAME METHODS =====
    VoxItem setDisplayName(Component name);

    Component getDisplayName();
}
