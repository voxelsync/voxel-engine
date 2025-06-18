/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.paper;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.leycm.storage.StorageSection;
import sync.voxel.api.VoEngine;
import sync.voxel.api.item.VoItemCreateReason;
import sync.voxel.api.item.VoItemStack;
import sync.voxel.api.material.VoMaterial;
import sync.voxel.api.material.VoRarity;
import sync.voxel.paper.item.VoxelItemStack;
import sync.voxel.paper.material.VoxelMaterial;

public class PaperEngine implements VoEngine {

    @Override
    public VoMaterial registerVoMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings) {
        return new VoxelMaterial(nameSpace, identifier, vaMaterial, settings);
    }

    @Override
    public VoMaterial registerVoMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings, VoRarity rarity) {
        return new VoxelMaterial(nameSpace, identifier, vaMaterial, settings, rarity);
    }

    @Override
    public VoItemStack registerVoItemStack(@NotNull VoMaterial voMaterial, int amount, VoItemCreateReason reason) {
        return new VoxelItemStack(voMaterial, amount, reason);
    }

}
