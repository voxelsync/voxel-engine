package sync.voxel.paper.runtime.item;

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;

import org.bukkit.attribute.Attribute;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import sync.voxel.api.runtime.item.VoItemCreateReason;
import sync.voxel.api.runtime.item.VoItemStack;
import sync.voxel.api.runtime.material.VoMaterial;

import java.time.LocalDateTime;
import java.util.*;

public class VoxelItemStack implements VoItemStack {
    private final String namespace;

    private final ItemStack stack;
    private ItemMeta meta;

    public VoxelItemStack(@NotNull VoMaterial voMaterial, int amount) {
        this.namespace = voMaterial.nameSpace();

        this.stack = new ItemStack(voMaterial.vaMaterial(), amount);
        this.meta = stack.getItemMeta();

        setPersistentData("voxel-meta", "uuid", PersistentDataType.STRING, UUID.randomUUID().toString());

        setPersistentData("voxel-meta", "create.timestamp", PersistentDataType.STRING, LocalDateTime.now().toString());
        setPersistentData("voxel-meta", "create.reason", PersistentDataType.STRING, VoItemCreateReason.CUSTOM.toString());

        setPersistentData("voxel-meta", "material", PersistentDataType.STRING,
                voMaterial.nameSpace() + ":" + voMaterial.identifier());
    }

    public VoxelItemStack(@NotNull VoMaterial voMaterial, int amount, VoItemCreateReason reason) {
        this.namespace = voMaterial.nameSpace();

        this.stack = new ItemStack(voMaterial.vaMaterial(), amount);
        this.meta = stack.getItemMeta();

        setPersistentData("voxel-meta", "uuid", PersistentDataType.STRING, UUID.randomUUID().toString());

        setPersistentData("voxel-meta", "create.timestamp", PersistentDataType.STRING, LocalDateTime.now().toString());
        setPersistentData("voxel-meta", "create.reason", PersistentDataType.STRING, reason);

        setPersistentData("voxel-meta", "material", PersistentDataType.STRING,
                voMaterial.nameSpace() + ":" + voMaterial.identifier());
    }

    @Override
    public ItemStack toItemStack() {
        return stack;
    }

    private void updateMeta() {
        meta = stack.getItemMeta();
    }

    private void applyMeta() {
        stack.setItemMeta(meta);
    }

    // ===== PERSISTENT DATA CONTAINER METHODS =====

    @NotNull
    public VoxelItemStack setPersistentData(@NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value) {
        return setPersistentData(namespace, key, type, value);
    }

    @NotNull
    private VoxelItemStack setPersistentData(@NotNull String namespace, @NotNull String key, @NotNull PersistentDataType<?, ?> type, @NotNull Object value) {
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
    public VoxelItemStack removePersistentData(@NotNull String key) {
        return removePersistentData(namespace, key);
    }

    @NotNull
    private VoxelItemStack removePersistentData(@NotNull String namespace, @NotNull String key) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(namespacedKey);
        return this;
    }

    @Nullable
    public <T> T getPersistentData(@NotNull String key, @NotNull PersistentDataType<?, T> type) {
        return getPersistentData(namespace, key, type);
    }

    @Nullable
    private <T> T getPersistentData(@NotNull String namespace, @NotNull String key, @NotNull PersistentDataType<?, T> type) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(namespacedKey, type);
    }

    public boolean hasPersistentData(@NotNull String key) {
        return hasPersistentData(namespace, key);
    }

    private boolean hasPersistentData(@NotNull String namespace, @NotNull String key) {
        updateMeta();
        NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(namespacedKey);
    }

    // ===== CONVENIENCE METHODS FOR  DATATYPES =====

    @NotNull
    public VoxelItemStack setString(@NotNull String key, @NotNull String value) {
        return setPersistentData(key, PersistentDataType.STRING, value);
    }

    @Nullable
    public String getString(@NotNull String key) {
        return getPersistentData(key, PersistentDataType.STRING);
    }

    @NotNull
    public VoxelItemStack setInt(@NotNull String key, int value) {
        return setPersistentData(key, PersistentDataType.INTEGER, value);
    }

    @Nullable
    public Integer getInt(@NotNull String key) {
        return getPersistentData(key, PersistentDataType.INTEGER);
    }

    @NotNull
    public VoxelItemStack setDouble(@NotNull String key, double value) {
        return setPersistentData(key, PersistentDataType.DOUBLE, value);
    }

    @Nullable
    public Double getDouble(@NotNull String key) {
        return getPersistentData(key, PersistentDataType.DOUBLE);
    }

    @NotNull
    public VoxelItemStack setLong(@NotNull String key, long value) {
        return setPersistentData(key, PersistentDataType.LONG, value);
    }

    @Nullable
    public Long getLong(@NotNull String key) {
        return getPersistentData(key, PersistentDataType.LONG);
    }

    @NotNull
    public VoxelItemStack setFloat(@NotNull String key, float value) {
        return setPersistentData(key, PersistentDataType.FLOAT, value);
    }

    @Nullable
    public Float getFloat(@NotNull String key) {
        return getPersistentData(key, PersistentDataType.FLOAT);
    }

    @NotNull
    public VoxelItemStack setBoolean(@NotNull String key, boolean value) {
        return setPersistentData(key, PersistentDataType.BYTE, (byte) (value ? 1 : 0));
    }

    public boolean getBoolean(@NotNull String key) {
        Byte value = getPersistentData(key, PersistentDataType.BYTE);
        return value != null && value == 1;
    }

    // ===== ITEM_BUILDER PART FOR DEFAULT EDITING =====

    public Component displayName() {
        updateMeta();
        return meta.displayName();
    }

    public void displayName(Component name) {
        meta.displayName(name);
        applyMeta();
    }

    public Component customName(){
        updateMeta();
        return meta.customName();
    }

    public void customName(Component name) {
        meta.customName(name);
        applyMeta();
    }

    public VoMaterial voMaterial() {
        return VoMaterial.valueOf(getPersistentData("voxel-meta", "material", PersistentDataType.STRING));
    }

    public int amount() {
        return stack.getAmount();
    }

    public void amount(int amount) {
        if (amount <= 0) throw new RuntimeException("An ItemStack can not have an amount 0 or lower");
        stack.setAmount(amount);
    }

    public List<Component> lore() {
        updateMeta();
        return meta.lore();
    }

    public void lore(Component... lores) {
        lore(Arrays.stream(lores).toList());
    }

    public void lore(List<Component> lores) {
        meta.lore(lores);
        applyMeta();
    }

    public void addLoreLine(Component line) {
        updateMeta();

        List<Component> lore = meta.lore() != null ? meta.lore() : new ArrayList<>();
	    assert lore != null;

	    lore.add(line);

        meta.lore(lore);
        applyMeta();
    }

    public void setLoreLine(int index, Component line) {
        updateMeta();
        List<Component> lore = meta.lore() != null ? meta.lore() : new ArrayList<>();
        assert lore != null;

        if (index >= lore.size()) {
            lore.add(line);
        } else if (line == null) {
            lore.remove(index);
        } else {
            lore.set(index, line);
        }

        meta.lore(lore);
        applyMeta();
    }

    public void removeLoreLine(int index) {
        setLoreLine(index, null);
    }

    public int customModelData() {
        updateMeta();
        return meta.hasCustomModelData() ? meta.getCustomModelData() : -1;
    }

    public void customModelData(int data) {
        meta.setCustomModelData(data);
        applyMeta();
    }

    public boolean isUnbreakable() {
        updateMeta();
        return meta.isUnbreakable();
    }

    public void unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        applyMeta();
    }

    public void addEnchant(Enchantment enchantment, int level) {
        updateMeta();
        meta.addEnchant(enchantment, level, true);
        applyMeta();
    }

    public void removeEnchant(Enchantment enchantment) {
        updateMeta();
        meta.removeEnchant(enchantment);
        applyMeta();
    }

    public boolean hasEnchant(Enchantment enchantment) {
        updateMeta();
        return meta.hasEnchant(enchantment);
    }

    public int getEnchantLevel(Enchantment enchantment) {
        updateMeta();
        return meta.getEnchantLevel(enchantment);
    }

    // ===== ATTRIBUTE MODIFIER METHODS =====

    public boolean hasAttributeModifiers() {
        updateMeta();
        return meta.hasAttributeModifiers();
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        updateMeta();
        return meta.getAttributeModifiers();
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        updateMeta();
        return meta.getAttributeModifiers(slot);
    }

    public Collection<AttributeModifier> getAttributeModifiers(Attribute attribute) {
        updateMeta();
        return meta.getAttributeModifiers(attribute);
    }

    public boolean addAttribute(@NotNull Attribute attribute, double value, AttributeModifier.Operation operation) {
        AttributeModifier modifier = new AttributeModifier(new NamespacedKey(namespace, attribute.getKey().toString()), value, operation);
        updateMeta();
        boolean result = meta.addAttributeModifier(attribute, modifier);
        applyMeta();
        return result;
    }

    public void setAttributeModifiers(Multimap<Attribute, AttributeModifier> modifiers) {
        updateMeta();
        meta.setAttributeModifiers(modifiers);
        applyMeta();
    }

    public boolean removeAttributeModifier(Attribute attribute) {
        updateMeta();
        boolean result = meta.removeAttributeModifier(attribute);
        applyMeta();
        return result;
    }

    public boolean removeAttributeModifier(EquipmentSlot slot) {
        updateMeta();
        boolean result = meta.removeAttributeModifier(slot);
        applyMeta();
        return result;
    }

    public boolean removeAttributeModifier(@NotNull Attribute attribute, double value, AttributeModifier.Operation operation) {
        AttributeModifier modifier = new AttributeModifier(new NamespacedKey(namespace, attribute.getKey().toString()), value, operation);
        updateMeta();
        boolean result = meta.removeAttributeModifier(attribute, modifier);
        applyMeta();
        return result;
    }

    public void clearAttributeModifiers() {
        updateMeta();
        meta.setAttributeModifiers(null);
        applyMeta();
    }

}
