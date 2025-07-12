/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.plugin.builder.vaconverter;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.api.common.VoxRenderType;
import sync.voxel.engine.api.enchantment.VoxEnchantment;
import sync.voxel.engine.api.material.VoxMaterial;
import sync.voxel.engine.plugin.utils.enchantment.VoxelEnchantment;
import sync.voxel.engine.plugin.utils.material.VoxelMaterial;

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
