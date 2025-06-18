/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.paper.pack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.leycm.storage.Storage;
import org.leycm.storage.StorageSection;
import org.leycm.storage.impl.JavaStorage;
import sync.voxel.api.VoxelEngine;
import sync.voxel.api.event.material.AdaptVaMaterialEvent;
import sync.voxel.api.material.VoMaterial;
import sync.voxel.api.material.VoRarity;

public class MaterialEngine {

    public static void reload(){
        VoMaterial.materials.clear();
        adaptVanillaMaterials();
    }

    public static void adaptVanillaMaterials(){

        for (Material vaMaterial : Material.values()) {

            String nameSpace = vaMaterial.getKey().getKey();
            String identifier = vaMaterial.getKey().getNamespace();
            VoRarity rarity = VoRarity.COMMON;
            StorageSection settings = Storage.of(
                    "empty", Storage.Type.JSON,
                    true, JavaStorage.class)
                    .getStorageSection("empty");

            try {
                rarity = VoRarity.valueOf(new ItemStack(vaMaterial)
                        .getItemMeta()
                        .getRarity()
                        .toString());

            } catch (Exception _){}

            AdaptVaMaterialEvent event = new AdaptVaMaterialEvent(
                    nameSpace, identifier,
                    vaMaterial, settings,
                    rarity
            );

            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) continue;

            VoxelEngine.registerVoMaterial(
                    event.getNameSpace(),
                    event.getIdentifier(),
                    event.getVaMaterial(),
                    event.getSettings(),
                    event.getRarity()
            );

        }

    }

}
