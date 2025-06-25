package sync.voxel.paper.utils.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sync.voxel.api.enchantment.VoEnchantment;
import sync.voxel.api.material.VoMaterial;
import sync.voxel.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.paper.runtime.material.VoxelMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VoxelItem {

    private VoMaterial voMaterial;

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

    public ItemStack toNewItemStack() {
        applyMeta();
        return stack.clone();
    }

    public ItemStack stack() {
        applyMeta();
        return stack;
    }

    public ItemMeta meta() {
        updateMeta();
        return meta;
    }

    private void updateMeta() {
        meta = stack.getItemMeta();
    }

    private void applyMeta() {
        stack.setItemMeta(meta);
    }

    private void updateLore() {
        lore = meta.lore() != null ? meta.lore() : new ArrayList<>();
        updateMeta();
    }

    private void applyLore() {
        meta.lore(lore); applyMeta();
    }

    // ===== PERSISTENT DATA CONTAINER METHODS =====

    @NotNull
    public VoxelItem setPersistentData(@NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value) {
        return setPersistentData(voMaterial.getKey().toString(), key, type, value);
    }

    @NotNull
    private VoxelItem setPersistentData(@NotNull String namespace, @NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value) {
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

    @NotNull
    public VoxelItem removePersistentData(@NotNull String key) {
        return removePersistentData(voMaterial.getKey().toString(), key);
    }

    @NotNull
    public VoxelItem removePersistentData(@NotNull String namespace, @NotNull String key) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(namespacedKey);
        return this;
    }

    @Nullable
    public <T> T getPersistentData(@NotNull String key, @NotNull PersistentDataType<?, T> type) {
        return getPersistentData(voMaterial.getKey().toString(), key, type);
    }

    @Nullable
    public <T> T getPersistentData(@NotNull String namespace, @NotNull String key, @NotNull PersistentDataType<?, T> type) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(namespacedKey, type);
    }

    public boolean hasPersistentData(@NotNull String key) {
        return hasPersistentData(voMaterial.getKey().toString(), key);
    }

    public boolean hasPersistentData(@NotNull String namespace, @NotNull String key) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(namespacedKey);
    }

    public VoxelItem addEnchant(@NotNull VoEnchantment voEnchant, @NotNull Integer level) {
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

        applyMeta();
        return this;
    }

    public VoxelItem removeEnchant(@NotNull VoEnchantment voEnchant) {
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

        applyMeta();
        return this;
    }


    // ===== VOXEL ENCHANTMENT METHODS =====

    public boolean hasEnchant(@NotNull ItemStack item, @NotNull VoxelEnchantment enchant) {
        Objects.requireNonNull(item, "ItemStack cannot be null");
        Objects.requireNonNull(enchant, "VoxelEnchantment cannot be null");

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        NamespacedKey key = new NamespacedKey("voxelenchant", enchant.getKey().toString('/'));
        return meta.getPersistentDataContainer().has(key);
    }

    public int getEnchantLevel(@NotNull ItemStack item, @NotNull VoxelEnchantment enchant) {
        Objects.requireNonNull(item, "ItemStack cannot be null");
        Objects.requireNonNull(enchant, "VoxelEnchantment cannot be null");

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }

        NamespacedKey key = new NamespacedKey("voxelenchant", enchant.getKey().toString('/'));
        Integer level = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        return level != null ? level : 0;
    }


    // ===== VOXEL MATERIAL METHODS =====

    @SuppressWarnings("UnstableApiUsage")
    public VoxelItem setVoMaterial(@NotNull VoMaterial voMaterial) {
        stack.setData(DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData().addString(voMaterial.getKey().toString()).build());
        applyMeta();
        return this;
    }

    @SuppressWarnings("UnstableApiUsage")
    public VoMaterial getVoMaterial() {
        updateMeta();
        CustomModelData data = stack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
        String id = data != null && !data.strings().isEmpty()
                ? data.strings().getFirst()
                : stack.getType().getKey().toString(); // fallback
        return VoxelMaterial.valueOf(id);
    }

    // ===== ITEM LORE METHODS =====

    public Component getLoreLine(int index) {
        updateLore();
        return lore.get(index);
    }

    public List<Component> getLore() {
        updateLore();
        return lore;
    }

    public VoxelItem addLoreLine(Component line) {
        updateLore();
        lore.add(line);
        applyLore();
        return this;
    }
    public VoxelItem addLoreLine(int index ,Component line) {
        updateLore();
        lore.add(index, line);
        applyLore();
        return this;
    }

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

    public VoxelItem removeLoreLine(int index) {
        updateLore();
        lore.remove(index);
        applyLore();
        return this;
    }
    public VoxelItem removeLastLoreLine() {
        updateLore();
        lore.removeLast();
        applyLore();
        return this;
    }

    public VoxelItem removeLoreLine(Component line) {
        updateLore();
        lore.removeIf(l1 -> l1.equals(line));
        applyLore();
        return this;
    }



    // ===== UPDATE / REFRESH ITEM METHODS =====

    private void updateEnchantmentItem() {
        updateMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
