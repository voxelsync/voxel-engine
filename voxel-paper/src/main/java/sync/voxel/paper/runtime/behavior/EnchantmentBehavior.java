package sync.voxel.paper.runtime.behavior;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import sync.voxel.api.enchantment.VoEnchantment;
import sync.voxel.paper.PaperPlugin;
import sync.voxel.paper.runtime.enchantment.VoxelEnchantment;

import javax.crypto.KEM;
import java.util.ArrayList;
import java.util.List;

import static sync.voxel.paper.runtime.enchantment.VoxelEnchantManager.voxEnchantManager;

public final class EnchantmentBehavior implements Listener {

    public static EnchantmentBehavior enchantmentBehavior;

    public static void register() {
        if (enchantmentBehavior == null) EnchantmentBehavior.enchantmentBehavior = new EnchantmentBehavior();
        Bukkit.getServer().getPluginManager().registerEvents(enchantmentBehavior, PaperPlugin.getPlugin(PaperPlugin.class));
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);
        ItemStack result = event.getResult();
        if ((firstItem.getType() == secondItem.getType()) || (secondItem.getType() == Material.ENCHANTED_BOOK)) {
            event.setResult(null);
            return;
        }
        for (VoEnchantment voEnchant : VoxelEnchantment.values()) {
            if (!EnchantmentTarget.valueOf(voEnchant.getAttribute("enchantment_target", String.class, "tool")).includes(firstItem)) {
                continue;
            }
            if (voxEnchantManager.hasEnchant(firstItem, voEnchant) && voxEnchantManager.hasEnchant(secondItem, voEnchant)) {
                int l1 = voxEnchantManager.getEnchantLevel(firstItem, voEnchant);
                int l2 = voxEnchantManager.getEnchantLevel(secondItem, voEnchant);
                voxEnchantManager.addEnchant(result, voEnchant, combineEnchantLevel(l1, l2, voEnchant.getAttribute("max_level", Integer.class, 1)));
                continue;
            }
            if (!voxEnchantManager.hasEnchant(firstItem, voEnchant) && voxEnchantManager.hasEnchant(secondItem, voEnchant)) {
                for (String enchant : (List<String>) voEnchant.getAttribute("incompatible_enchants", List.class, new ArrayList<>())) {
                    if (voxEnchantManager.hasEnchant(firstItem, VoxelEnchantment.valueOf(enchant))) {
                        event.setResult(null);
                        return;
                    }
                }
                voxEnchantManager.addEnchant(result, voEnchant, voxEnchantManager.getEnchantLevel(secondItem, voEnchant));
            }
        }
        event.setResult(result);
    }

    private Integer combineEnchantLevel(Integer l1, Integer l2, Integer maxLevel) {
        if (l1 > l2) {
            return l1;
        }
        else if (l2 > l1) {
            return l2;
        }
        else if (l2 == l1) {
            return l1+1 <= maxLevel ? l1+1 : l1;
        }
        else {
            return l1;
        }
    }


}
