/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.paper;

import org.bukkit.inventory.ItemStack;
import sync.voxel.engine.api.VoxEngine;
import sync.voxel.engine.api.utils.item.VoxItem;
import sync.voxel.engine.paper.utils.voxel.VoxelItem;

public class PaperEngine implements VoxEngine {

    @Override
    public VoxItem voxItemTunnel(ItemStack stack) {
        return VoxelItem.edit(stack);
    }
}
