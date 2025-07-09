/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.plugin.utils.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import sync.voxel.engine.api.utils.item.VoxItem;
import sync.voxel.engine.api.enchantment.VoxEnchantment;
import sync.voxel.engine.api.material.VoxMaterial;
import sync.voxel.engine.plugin.utils.enchantment.VoxelEnchantment;
import sync.voxel.engine.plugin.utils.material.VoxelMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VoxelItem implements VoxItem {

    private VoxMaterial voMaterial;

    private final ItemStack stack;
    private ItemMeta meta;
    private List<Component> lore;

    @Contract("_ -> new")
    public static @NotNull VoxelItem edit(@NotNull ItemStack item) {
        return new VoxelItem(item);
    }

    @Contract("_ -> new")
    public static @NotNull VoxelItem warp(@NotNull ItemStack item) {
        return new VoxelItem(item);
    }

    @Contract("_ -> new")
    public static @NotNull VoxelItem clone(@NotNull ItemStack item) {
        return new VoxelItem(item.clone());
    }
    private VoxelItem(@NotNull ItemStack stack) {
        this.stack = stack;
        this.meta = stack.getItemMeta();

        updateMaterialItem();
        updateEnchantmentItem();
    }

    @Override
    public ItemStack toNewItemStack() {
        applyMeta();
        return stack.clone();
    }

    @Override
    public ItemStack stack() {
        applyMeta();
        return stack;
    }

    @Override
    public ItemMeta meta() {
        updateMeta();
        return meta;
    }

    private void updateMeta() {
        meta = stack.getItemMeta();
        if (meta == null) throw new IllegalStateException("ItemMeta is null for item type: " + stack.getType()
                    + ". This is likely caused by an item type that does not support meta, such as AIR.");
    }

    private void applyMeta() {
        stack.setItemMeta(meta);
    }

    private void updateLore() {
        updateMeta();
        lore = meta.lore() != null ? meta.lore() : new ArrayList<>();
    }

    private void applyLore() {
        meta.lore(lore); applyMeta();
        applyMeta();
    }


    // ===== PERSISTENT DATA CONTAINER METHODS =====

    @Override
    @NotNull
    public VoxelItem setPersistentData(@NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value) {
        return setPersistentData(voMaterial.getKey().toString(), key, type, value);
    }

    @Override
    @NotNull
    public VoxelItem setPersistentData(@NotNull String namespace, @NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (type == PersistentDataType.STRING) {
            pdc.set(namespacedKey, PersistentDataType.STRING, (String) value);
        } else if (type == PersistentDataType.INTEGER) {
            pdc.set(namespacedKey, PersistentDataType.INTEGER, (Integer) value);
        } else if (type == PersistentDataType.DOUBLE) {
            pdc.set(namespacedKey, PersistentDataType.DOUBLE, (Double) value);
        } else if (type == PersistentDataType.FLOAT) {
            pdc.set(namespacedKey, PersistentDataType.FLOAT, (Float) value);
        } else if (type == PersistentDataType.LONG) {
            pdc.set(namespacedKey, PersistentDataType.LONG, (Long) value);
        } else if (type == PersistentDataType.SHORT) {
            pdc.set(namespacedKey, PersistentDataType.SHORT, (Short) value);
        } else if (type == PersistentDataType.BYTE) {
            pdc.set(namespacedKey, PersistentDataType.BYTE, (Byte) value);
        } else if (type == PersistentDataType.BYTE_ARRAY) {
            pdc.set(namespacedKey, PersistentDataType.BYTE_ARRAY, (byte[]) value);
        } else if (type == PersistentDataType.INTEGER_ARRAY) {
            pdc.set(namespacedKey, PersistentDataType.INTEGER_ARRAY, (int[]) value);
        } else if (type == PersistentDataType.LONG_ARRAY) {
            pdc.set(namespacedKey, PersistentDataType.LONG_ARRAY, (long[]) value);
        }

        return this;
    }

    @Override
    @NotNull
    public VoxelItem removePersistentData(@NotNull String key) {
        return removePersistentData(voMaterial.getKey().toString(), key);
    }

    @Override
    @NotNull
    public VoxelItem removePersistentData(@NotNull String namespace, @NotNull String key) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(namespacedKey);
        return this;
    }

    @Override
    @Nullable
    public <T> T getPersistentData(@NotNull String key, @NotNull PersistentDataType<?, T> type) {
        return getPersistentData(voMaterial.getKey().toString(), key, type);
    }

    @Override
    @Nullable
    public <T> T getPersistentData(@NotNull String namespace, @NotNull String key, @NotNull PersistentDataType<?, T> type) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(namespacedKey, type);
    }

    @Override
    public boolean hasPersistentData(@NotNull String key) {
        return hasPersistentData(voMaterial.getKey().toString(), key);
    }

    @Override
    public boolean hasPersistentData(@NotNull String namespace, @NotNull String key) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(namespacedKey);
    }


    // ===== VOXEL ENCHANTMENT METHODS =====

    @Override
    public VoxelItem addEnchant(@NotNull VoxEnchantment voEnchant, @NotNull Integer level) {
        Objects.requireNonNull(voEnchant, "VoxelEnchantment cannot be null");
        Objects.requireNonNull(level, "Enchantment level cannot be null");
        updateMeta();

        String enchantKey = voEnchant.getKey().toString();

        NamespacedKey customKey = new NamespacedKey("voxelenchant", voEnchant.getKey().toString('/'));
        meta.getPersistentDataContainer().set(customKey, PersistentDataType.INTEGER, level);

        if (enchantKey.startsWith("minecraft:")) { // Apply as Vanilla
            NamespacedKey vanillaKey = NamespacedKey.fromString(enchantKey);
            if (vanillaKey == null) throw new IllegalArgumentException("Invalid vanilla enchantment key: " + enchantKey);

            Enchantment vanillaEnchant = VoxelEnchantment.VANILLA_ENCHANTMENTS.getOrThrow(vanillaKey);
            meta.addEnchant(vanillaEnchant, level, true);
        }

        meta.setEnchantmentGlintOverride(true);

        applyMeta();
        return this;
    }

    @Override
    public VoxelItem removeEnchant(@NotNull VoxEnchantment voEnchant) {
        Objects.requireNonNull(voEnchant, "VoxelEnchantment cannot be null");
        updateMeta();

        String enchantKey = voEnchant.getKey().toString();
        NamespacedKey customKey = new NamespacedKey("voxelenchant", voEnchant.getKey().toString('/'));
        meta.getPersistentDataContainer().remove(customKey);

        if (enchantKey.startsWith("minecraft:")) {
            NamespacedKey vanillaKey = NamespacedKey.fromString(enchantKey);
            if (vanillaKey != null) {
                Enchantment vanillaEnchant = VoxelEnchantment.VANILLA_ENCHANTMENTS.getOrThrow(vanillaKey);
                if (meta.hasEnchant(vanillaEnchant)) meta.removeEnchant(vanillaEnchant);
            }
        }

        if (!hasAnyEnchant() && !meta.hasEnchants()) {
            meta.setEnchantmentGlintOverride(false);
        }

        applyMeta();
        return this;
    }

    @Override
    public boolean hasEnchant(@NotNull VoxEnchantment enchant) {
        Objects.requireNonNull(enchant, "VoxelEnchantment cannot be null");

        updateMeta();
        NamespacedKey key = new NamespacedKey("voxelenchant", enchant.getKey().toString('/'));

        return meta.getPersistentDataContainer().has(key);
    }

    public boolean hasAnyEnchant() {

        updateMeta();
        if (!meta.getEnchants().isEmpty()) return true;

        for (VoxEnchantment enchant : VoxelEnchantment.values()) {
            NamespacedKey key = new NamespacedKey("voxelenchant", enchant.getKey().toString('/'));
            if (meta.getPersistentDataContainer().has(key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getEnchantLevel(@NotNull VoxEnchantment enchant) {
        Objects.requireNonNull(enchant, "VoxelEnchantment cannot be null");

        updateMeta();
        NamespacedKey key = new NamespacedKey("voxelenchant", enchant.getKey().toString('/'));
        Integer level = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

        return level != null ? level : 0;
    }


    // ===== VOXEL MATERIAL METHODS =====

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public VoxelItem setVoMaterial(@NotNull VoxMaterial voMaterial) {
        CustomModelData modelData = CustomModelData.customModelData().addString(voMaterial.getKey().toString()).build();
        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, modelData);
        applyMeta();
        return this;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public VoxMaterial getVoMaterial() {
        updateMeta();
        CustomModelData data = stack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
        String id = data != null && !data.strings().isEmpty()
                ? data.strings().getFirst()
                : stack.getType().getKey().toString(); // fallback
        return VoxelMaterial.valueOf(id);
    }

    // ===== ITEM LORE METHODS =====

    @Override
    public List<Component> getLore() {
        updateLore();
        return lore;
    }

    @Override
    public void setLore(List<Component> lore) {
        this.lore = lore;
        applyLore();
    }

    @Override
    public Component getLoreLine(int index) {
        updateLore();
        return lore.get(index);
    }

    @Override
    public VoxelItem addLoreLine(Component line) {
        updateLore();
        lore.add(line);
        applyLore();
        return this;
    }

    @Override
    public VoxelItem addLoreLine(int index ,Component line) {
        updateLore();
        lore.add(index, line);
        applyLore();
        return this;
    }

    @Override
    public VoxelItem setLoreLine(int index, Component line) {
        updateLore();
        if (line != null) {
            lore.set(index, line);
        } else {
            removeLoreLine(index);
        }
        applyLore();
        return this;
    }

    @Override
    public VoxelItem removeLoreLine(int index) {
        updateLore();
        lore.remove(index);
        applyLore();
        return this;
    }

    @Override
    public VoxelItem removeLastLoreLine() {
        updateLore();
        lore.removeLast();
        applyLore();
        return this;
    }

    @Override
    public VoxelItem removeLoreLine(Component line) {
        updateLore();
        lore.removeIf(l1 -> l1.equals(line));
        applyLore();
        return this;
    }

    // ===== ITEM NAME / DISPLAY_NAME METHODS =====

    @Override
    public VoxelItem setDisplayName(Component name){
        meta.displayName(name);
        applyMeta();
        return this;
    }

    @Override
    public Component getDisplayName(){
        updateMeta();
        return meta.displayName();
    }


    // ===== UPDATE / REFRESH ITEM METHODS =====

    private void updateEnchantmentItem() {
        updateMeta();
        for (@NotNull Enchantment enchantment : VoxelEnchantment.VANILLA_ENCHANTMENTS) {
            if (meta.hasEnchant(enchantment)) {
                VoxelItem item = addEnchant(
                        VoxelEnchantment.valueOf(enchantment),
                        meta.getEnchantLevel(enchantment)
                );
            }
        }
        applyMeta();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void updateMaterialItem() {
        updateMeta();
        CustomModelData data = stack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
        String id = data != null && !data.strings().isEmpty()
                ? data.strings().getFirst()
                : stack.getType().getKey().toString(); // fallback

        this.voMaterial = VoxelMaterial.valueOf(id);

        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData().addString(voMaterial.getKey().toString()).build());
        applyMeta();
    }


}
