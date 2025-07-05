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

public final class EnchantmentBehavior implements Listener {

    public static EnchantmentBehavior enchantmentBehavior;

    public static void register() {
        if (enchantmentBehavior == null) EnchantmentBehavior.enchantmentBehavior = new EnchantmentBehavior();
        Bukkit.getServer().getPluginManager().registerEvents(enchantmentBehavior, PaperPlugin.getPlugin(PaperPlugin.class));
    }

    @EventHandler
    public void onAnvilPrepare(@NotNull PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);
        ItemStack result = event.getResult();
        assert firstItem != null;

        if (result == null) return;
        if (secondItem == null) return;

        if (firstItem.getType().equals(secondItem.getType()) || secondItem.getType().equals(Material.ENCHANTED_BOOK)) {
            result = combineTwoItems(event, firstItem, secondItem, result);
            System.out.println("haha ");
        }
        System.out.println("out und so fertuig");
        event.setResult(result);
    }

    private @Nullable ItemStack combineTwoItems(PrepareAnvilEvent event, ItemStack firstItem, ItemStack secondItem, ItemStack result){
        for (VoxEnchantment voEnchant : VoxelEnchantment.values()) {
            if (!EnchantmentTarget.valueOf(voEnchant.getAttribute("enchantment-target", String.class, "tool")).includes(firstItem)) {
                continue;
            }

            boolean firstHas = VoxelItem.edit(firstItem).hasEnchant(voEnchant);
            boolean secondHas = VoxelItem.edit(secondItem).hasEnchant(voEnchant);

            System.out.println("firstItemHas: " + firstItem + " secondItemHas: " + secondHas);

            if (firstHas && secondHas) {
                int firstEnchantLevel = VoxelItem.edit(firstItem).getEnchantLevel(voEnchant);
                int secondEnchantLevel = VoxelItem.edit(secondItem).getEnchantLevel(voEnchant);
                int combined = combineEnchantLevel(firstEnchantLevel, secondEnchantLevel, voEnchant.getAttribute("max-level", Integer.class, 1));
                VoxelItem.edit(result).addEnchant(voEnchant, combined);
                continue;
            }

            if (!firstHas && secondHas) {
                //noinspection unchecked
                for (String incompatibleEnchant : (List<String>) voEnchant.getAttribute("incompatible-enchants", List.class, new ArrayList<>())) {
                    if (VoxelItem.edit(firstItem).hasEnchant(VoxelEnchantment.valueOf(incompatibleEnchant))) return null;
                }

                int level = VoxelItem.edit(secondItem).getEnchantLevel(voEnchant);
                VoxelItem.edit(result).addEnchant(voEnchant, level);
            }
        }

        return result;
    }


    private int combineEnchantLevel(int l1, int l2, int maxLevel) {
        if (l1 > l2) {
            return l1;
        } else if (l2 > l1) {
            return l2;
        }
        
        return l1+1 <= maxLevel ? l1+1 : l1;
    }


}
