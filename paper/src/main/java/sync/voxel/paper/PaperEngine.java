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
import org.leycm.storage.StorageSection;
import sync.voxel.api.VoEngine;
import sync.voxel.api.item.VoMaterial;
import sync.voxel.api.item.VoRarity;
import sync.voxel.paper.item.VoxelMaterial;

public class PaperEngine implements VoEngine {

    @Override
    public VoMaterial registerVoMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings) {
        return new VoxelMaterial(nameSpace, identifier, vaMaterial, settings);
    }

    @Override
    public VoMaterial registerVoMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings, VoRarity rarity) {
        return new VoxelMaterial(nameSpace, identifier, vaMaterial, settings, rarity);
    }

}
