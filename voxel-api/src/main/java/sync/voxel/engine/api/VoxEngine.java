/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.api;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import sync.voxel.engine.api.utils.item.VoxItem;

public interface VoxEngine {

    @ApiStatus.Internal
    VoxItem voxItemTunnel(ItemStack stack);
}
