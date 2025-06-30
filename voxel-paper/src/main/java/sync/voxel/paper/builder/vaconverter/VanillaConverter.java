package sync.voxel.paper.builder.vaconverter;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import sync.voxel.api.common.VoxKey;
import sync.voxel.api.common.VoxRenderType;
import sync.voxel.api.enchantment.VoxEnchantment;
import sync.voxel.api.material.VoxMaterial;
import sync.voxel.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.paper.runtime.material.VoxelMaterial;

public class VanillaConverter {

    public static void convert() {
        convertEnchants();
        convertMaterials();
    }

    private static void convertEnchants() {
        for (Enchantment enchantment : Enchantment.values()) {
            VoxEnchantment voxEnchantment = VoxelEnchantment.forkEnchantment(VoxKey.of(enchantment.getKey().toString()));
        }
    }

    private static void convertMaterials() {
        for (Material material : Material.values()) {
            VoxMaterial voMaterial = VoxelMaterial.forkMaterial(material, VoxKey.of(material.getKey()), VoxRenderType.NONE);
        }
    }

}
