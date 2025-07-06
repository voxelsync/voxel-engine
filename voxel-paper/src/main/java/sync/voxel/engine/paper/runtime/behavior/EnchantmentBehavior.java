package sync.voxel.engine.paper.runtime.behavior;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sync.voxel.engine.api.enchantment.VoxEnchantment;
import sync.voxel.engine.paper.PaperPlugin;
import sync.voxel.engine.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.engine.paper.utils.item.VoxelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class EnchantmentBehavior implements Listener {

    private static EnchantmentBehavior instance;

    private EnchantmentBehavior() {}

    public static void register() {
        if (instance == null) {
            instance = new EnchantmentBehavior();
            Bukkit.getServer().getPluginManager().registerEvents(instance, PaperPlugin.getPlugin(PaperPlugin.class));
        }
    }

    @EventHandler
    public void onAnvilPrepare(@NotNull PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);
        ItemStack result = event.getResult();

        if (firstItem == null || secondItem == null || result == null) {
            return;
        }

        if (canCombineItems(firstItem, secondItem)) {
            ItemStack newResult = combineItems(firstItem, secondItem, result);
            event.setResult(newResult);
        }
    }

    private boolean canCombineItems(@NotNull ItemStack firstItem, @NotNull ItemStack secondItem) {
        if (firstItem.getType().equals(secondItem.getType())) {
            return true;
        }

        return secondItem.getType().equals(Material.ENCHANTED_BOOK);
    }

    private @Nullable ItemStack combineItems(@NotNull ItemStack firstItem, @NotNull ItemStack secondItem, @NotNull ItemStack result) {
        ItemStack finalResult = result.clone();

        for (VoxEnchantment voxEnchant : VoxelEnchantment.values()) {
            try {
                if (!isEnchantmentApplicable(voxEnchant, firstItem, secondItem)) {
                    continue;
                }

                boolean firstHasEnchant = VoxelItem.edit(firstItem).hasEnchant(voxEnchant);
                boolean secondHasEnchant = VoxelItem.edit(secondItem).hasEnchant(voxEnchant);

                if (firstHasEnchant && secondHasEnchant) {
                    if (!combineEnchantmentLevels(voxEnchant, firstItem, secondItem, finalResult)) {
                        return null;
                    }
                } else if (firstHasEnchant) {
                    if (!transferEnchantment(voxEnchant, firstItem, finalResult)) {
                        return null;
                    }
                } else if (secondHasEnchant) {
                    if (!addEnchantmentFromSecondItem(voxEnchant, firstItem, secondItem, finalResult)) {
                        return null;
                    }
                }

            } catch (Exception e) {
                Bukkit.getServer().getLogger().log(Level.WARNING, "Fehler beim Verarbeiten von Enchantment " + voxEnchant.getKey(), e);
                continue;
            }
        }

        return finalResult;
    }

    private boolean isEnchantmentApplicable(@NotNull VoxEnchantment voxEnchant, @NotNull ItemStack firstItem, @NotNull ItemStack secondItem) {
        try {
            String targetString = voxEnchant.getAttribute("enchantment-target", String.class, "TOOL");
            EnchantmentTarget target = EnchantmentTarget.valueOf(targetString.toUpperCase());

            if (secondItem.getType() == Material.ENCHANTED_BOOK) {
                return target.includes(firstItem);
            }

            return target.includes(firstItem) && target.includes(secondItem);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean combineEnchantmentLevels(@NotNull VoxEnchantment voxEnchant, @NotNull ItemStack firstItem, @NotNull ItemStack secondItem, @NotNull ItemStack result) {
        int firstLevel = VoxelItem.edit(firstItem).getEnchantLevel(voxEnchant);
        int secondLevel = VoxelItem.edit(secondItem).getEnchantLevel(voxEnchant);
        int maxLevel = voxEnchant.getAttribute("max-level", Integer.class, 1);

        int combinedLevel = calculateCombinedLevel(firstLevel, secondLevel, maxLevel);

        VoxelItem.edit(result).addEnchant(voxEnchant, combinedLevel);
        return true;
    }

    private boolean transferEnchantment(@NotNull VoxEnchantment voxEnchant, @NotNull ItemStack sourceItem, @NotNull ItemStack result) {
        int level = VoxelItem.edit(sourceItem).getEnchantLevel(voxEnchant);
        VoxelItem.edit(result).addEnchant(voxEnchant, level);
        return true;
    }

    private boolean addEnchantmentFromSecondItem(@NotNull VoxEnchantment voxEnchant, @NotNull ItemStack firstItem, @NotNull ItemStack secondItem, @NotNull ItemStack result) {
        // Prüfe Inkompatibilitäten mit bereits vorhandenen Enchantments
        if (!isEnchantmentCompatible(voxEnchant, firstItem, result)) {
            return false;
        }

        int level = VoxelItem.edit(secondItem).getEnchantLevel(voxEnchant);
        VoxelItem.edit(result).addEnchant(voxEnchant, level);
        return true;
    }

    private boolean isEnchantmentCompatible(@NotNull VoxEnchantment voxEnchant, @NotNull ItemStack firstItem, @NotNull ItemStack result) {
        try {
            @SuppressWarnings("unchecked")
            List<String> incompatibleEnchants = (List<String>) voxEnchant.getAttribute("incompatible-enchants", List.class, new ArrayList<>());

            for (String incompatibleEnchantName : incompatibleEnchants) {
                try {
                    VoxEnchantment incompatibleEnchant = VoxelEnchantment.valueOf(incompatibleEnchantName);

                    if (VoxelItem.edit(firstItem).hasEnchant(incompatibleEnchant) ||
                            VoxelItem.edit(result).hasEnchant(incompatibleEnchant)) {
                        return false;
                    }
                } catch (IllegalArgumentException _) {}
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
}