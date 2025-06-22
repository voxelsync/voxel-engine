package sync.voxel.paper.runtime.enchantment;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sync.voxel.api.enchantment.VoEnchantment;

import java.util.List;

public final class VoxelEnchantManager {

    public static final VoxelEnchantManager voxEnchantManager = new VoxelEnchantManager();

    public ItemStack addEnchant(@NotNull ItemStack item, @NotNull VoEnchantment enchant, Integer level) {
        ItemMeta meta = item.getItemMeta();
        List<String> lores = meta.getLore();
        lores.removeIf(lore -> lore.startsWith(ChatColor.GRAY + "%voxelenchant:" + enchant.getKey().toString() + "%"));
        lores.add(0,  ChatColor.GRAY + "%voxelenchant:" + enchant.getKey().toString() + "% " + VoxelEnchantment.getRomanInteger(level));
        meta.getPersistentDataContainer().set(new NamespacedKey("voxelenchant", enchant.getKey().toString('/')), PersistentDataType.INTEGER, level);
        meta.setLore(lores);
        meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack removeEnchant(@NotNull ItemStack item, @NotNull VoEnchantment enchant) {
        ItemMeta meta = item.getItemMeta();
        List<String> lores = meta.getLore();
        lores.removeIf(lore -> lore.startsWith(ChatColor.GRAY + "%voxelenchant:" + enchant.getKey().toString() + "%"));
        meta.getPersistentDataContainer().remove(new NamespacedKey("voxelenchant", enchant.getKey().toString('/')));
        if (!hasAnyVoxelEnchants(item) && !meta.hasEnchants()) {
            meta.setEnchantmentGlintOverride(false);
        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    public boolean hasEnchant(@NotNull ItemStack item, @NotNull VoEnchantment enchant) {
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("voxelenchant", enchant.getKey().toString('/')));
    }
    public Integer getEnchantLevel(ItemStack item, VoEnchantment enchant) {
        if (hasEnchant(item, enchant)) {
            return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("voxelenchant", enchant.getKey().toString('/')), PersistentDataType.INTEGER);
        }
        return 0;
    }

    public boolean hasAnyVoxelEnchants(ItemStack item) {
        for (VoEnchantment value : VoxelEnchantment.values()) {
            if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("voxelenchant", value.getKey().toString('/')))) {
                return true;
            }
        }
        return false;
    }

    @Contract("_ -> param1")
    public @NotNull ItemStack updateItem(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        for (@NotNull Enchantment value : RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)) {
            if (meta.hasEnchant(value)) {
                addEnchant(item, VoxelEnchantment.valueOf(value), meta.getEnchantLevel(value));
            }
        }
        return item;
    }

}
