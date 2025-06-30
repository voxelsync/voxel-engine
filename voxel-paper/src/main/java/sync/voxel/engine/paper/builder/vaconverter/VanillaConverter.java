package sync.voxel.engine.paper.builder.vaconverter;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.api.common.VoxRenderType;
import sync.voxel.engine.api.enchantment.VoxEnchantment;
import sync.voxel.engine.api.material.VoxMaterial;
import sync.voxel.engine.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.engine.paper.runtime.material.VoxelMaterial;

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
