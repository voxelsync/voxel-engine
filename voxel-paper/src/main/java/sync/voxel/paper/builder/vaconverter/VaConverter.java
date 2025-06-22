package sync.voxel.paper.builder.vaconverter;

import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class VaConverter {

    public static void convert() {

    }

    public static void convertEnchants() {
        for (Material material : Material.values()) {

        }
    }

    public static void convertMaterials() {
        for (Enchantment enchantment : RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)) {

        }
    }

}
