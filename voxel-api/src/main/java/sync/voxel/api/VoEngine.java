/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.leycm.storage.StorageSection;

import sync.voxel.api.runtime.item.VoItemCreateReason;
import sync.voxel.api.runtime.item.VoItemStack;
import sync.voxel.api.runtime.material.VoMaterial;
import sync.voxel.api.runtime.material.VoRarity;

public interface VoEngine {
    VoMaterial registerVoMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings);

    VoMaterial registerVoMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings, VoRarity rarity);

    VoItemStack registerVoItemStack(@NotNull VoMaterial voMaterial, int amount, VoItemCreateReason reason);
}
