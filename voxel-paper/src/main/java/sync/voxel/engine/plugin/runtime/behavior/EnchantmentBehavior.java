/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.plugin.runtime.behavior;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

import sync.voxel.engine.api.enchantment.VoxEnchantment;
import sync.voxel.engine.plugin.PaperPlugin;
import sync.voxel.engine.plugin.utils.enchantment.VoxelEnchantment;
import sync.voxel.engine.plugin.utils.item.VoxelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class EnchantmentBehavior implements Listener {

    private static EnchantmentBehavior instance;

    private EnchantmentBehavior() {}

    public static void register() {
        if (instance == null) {
            instance = new EnchantmentBehavior();
            Bukkit.getServer().getPluginManager().registerEvents(instance, PaperPlugin.getPlugin(PaperPlugin.class));
        }
    }

    @Deprecated(since = "1.0.0") // never use its final now
    public static void unregister() {
        if (instance != null) {
            instance = null;
        }
    }

    @EventHandler
    public void onAnvilPrepare(@NotNull PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack leftItem = inventory.getItem(0);
        ItemStack rightItem = inventory.getItem(1);

        if (leftItem == null || rightItem == null) return;

        if (!canCombineItems(leftItem, rightItem)) return;

        ItemStack result = createBaseResult(leftItem, rightItem);

        if (!combineEnchantments(leftItem, rightItem, result)) {
            event.setResult(null);
            return;
        }

        int xpCost = calculateXpCost(leftItem, rightItem, result);
        //inventory.setRepairCost(xpCost);

        if (!Objects.requireNonNull(event.getView().getRenameText(), "Anvil RenameText can not be null").strip().isEmpty()) {
            result.editMeta(meta -> meta.displayName(Component.text(event.getView().getRenameText())));
            xpCost++;
        } else {
            result.editMeta(meta -> meta.displayName(null));
        }

        event.getView().setRepairCost(xpCost);

        event.setResult(result);
    }

    private boolean canCombineItems(@NotNull ItemStack leftItem, @NotNull ItemStack rightItem) {
        if (VoxelItem.edit(leftItem).getVoMaterial().getKey().toString().equals(VoxelItem.edit(rightItem).getVoMaterial().getKey().toString())) {
            return true;
        }

        if (rightItem.getType() == Material.ENCHANTED_BOOK) {
            return hasCompatibleEnchantments(leftItem, rightItem);
        }

        return false;
    }

    private boolean hasCompatibleEnchantments(@NotNull ItemStack targetItem, @NotNull ItemStack enchantedBook) {
        for (VoxEnchantment enchant : VoxelEnchantment.values()) {
            if (VoxelItem.edit(enchantedBook).hasEnchant(enchant)) {
                if (isEnchantmentApplicable(enchant, targetItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    private @NotNull ItemStack createBaseResult(@NotNull ItemStack leftItem, @NotNull ItemStack rightItem) {
        ItemStack result = leftItem.clone();

        if (leftItem.getType() == rightItem.getType()) {
            combineDurability(result, leftItem, rightItem);
        }

        return result;
    }

    private void combineDurability(@NotNull ItemStack result, @NotNull ItemStack leftItem, @NotNull ItemStack rightItem) {
        if (leftItem.getType().getMaxDurability() > 0) {
            short leftDurability = leftItem.getDurability();
            short rightDurability = rightItem.getDurability();
            short maxDurability = leftItem.getType().getMaxDurability();

            int newDamage = Math.max(0, Math.min(leftDurability, rightDurability) - (maxDurability * 12 / 100));
            result.setDurability((short) newDamage);
        }
    }

    private boolean combineEnchantments(@NotNull ItemStack leftItem, @NotNull ItemStack rightItem, @NotNull ItemStack result) {
        boolean successfulCombination = false;

        for (VoxEnchantment enchant : VoxelEnchantment.values()) {
            if (VoxelItem.edit(leftItem).hasEnchant(enchant)) {
                int level = VoxelItem.edit(leftItem).getEnchantLevel(enchant);
                VoxelItem.edit(result).addEnchant(enchant, level);
            }
        }

        for (VoxEnchantment enchant : VoxelEnchantment.values()) {
            if (!VoxelItem.edit(rightItem).hasEnchant(enchant)) {
                continue;
            }

            if (!isEnchantmentApplicable(enchant, leftItem)) {
                continue;
            }

            int rightLevel = VoxelItem.edit(rightItem).getEnchantLevel(enchant);

            if (VoxelItem.edit(result).hasEnchant(enchant)) {
                int leftLevel = VoxelItem.edit(result).getEnchantLevel(enchant);
                int maxLevel = enchant.getAttribute("max-level", Integer.class, 1);
                int newLevel = calculateCombinedLevel(leftLevel, rightLevel, maxLevel);

                VoxelItem.edit(result).addEnchant(enchant, newLevel);
                successfulCombination = true;
            } else {
                if (isEnchantmentCompatibleWithResult(enchant, result)) {
                    VoxelItem.edit(result).addEnchant(enchant, rightLevel);
                    successfulCombination = true;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isEnchantmentApplicable(@NotNull VoxEnchantment enchant, @NotNull ItemStack item) {
        try {
            String targetString = enchant.getAttribute("enchantment-target", String.class, "TOOL");
            EnchantmentTarget target = EnchantmentTarget.valueOf(targetString.toUpperCase());
            return target.includes(item);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isEnchantmentCompatibleWithResult(@NotNull VoxEnchantment enchant, @NotNull ItemStack result) {
        try {
            @SuppressWarnings("unchecked")
            List<String> incompatibleEnchants = (List<String>) enchant.getAttribute("incompatible-enchants", List.class, new ArrayList<>());

            for (String incompatibleName : incompatibleEnchants) {
                try {
                    VoxEnchantment incompatibleEnchant = VoxelEnchantment.valueOf(incompatibleName);
                    if (VoxelItem.edit(result).hasEnchant(incompatibleEnchant)) {
                        return false;
                    }
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }

            return true;
        } catch (ClassCastException e) {
            return true;
        }
    }

    private int calculateCombinedLevel(int level1, int level2, int maxLevel) {
        if (level1 == level2) {
            return Math.min(level1 + 1, maxLevel);
        } else {
            return Math.max(level1, level2);
        }
    }

    private int calculateXpCost(@NotNull ItemStack leftItem, @NotNull ItemStack rightItem, @NotNull ItemStack result) {
        int baseCost = 0;

        if (leftItem.getType() == rightItem.getType() && leftItem.getType().getMaxDurability() > 0) {
            baseCost += 2;
        }

        for (VoxEnchantment enchant : VoxelEnchantment.values()) {
            if (VoxelItem.edit(result).hasEnchant(enchant)) {
                int level = VoxelItem.edit(result).getEnchantLevel(enchant);
                int rarity = enchant.getAttribute("rarity", Integer.class, 1);
                baseCost += level * rarity;
            }
        }

        int leftPriorWork = getItemPriorWork(leftItem);
        int rightPriorWork = getItemPriorWork(rightItem);
        int priorWorkPenalty = Math.max(leftPriorWork, rightPriorWork);

        baseCost += priorWorkPenalty;

        return Math.max(1, baseCost);
    }

    private int getItemPriorWork(@NotNull ItemStack item) {
        return 0;
    }
}