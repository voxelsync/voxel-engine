package sync.voxel.paper.builder.vaconverter;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import sync.voxel.api.common.VoKey;
import sync.voxel.api.common.VoRenderType;
import sync.voxel.api.enchantment.VoEnchantment;
import sync.voxel.api.material.VoMaterial;
import sync.voxel.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.paper.runtime.material.VoxelMaterial;

public class VanillaConverter {

    public static void convert() {
        convertEnchants();
        convertMaterials();
    }

    private static void convertEnchants() {
        for (Enchantment enchantment : Enchantment.values()) {
            VoEnchantment voEnchantment = VoxelEnchantment.forkEnchantment(VoKey.of(enchantment.getKey().toString()));
        }
    }

    private static void convertMaterials() {
        for (Material material : Material.values()) {
            VoMaterial voMaterial = VoxelMaterial.forkMaterial(material, VoKey.of(material.getKey()), VoRenderType.NONE);
        }
    }

}
