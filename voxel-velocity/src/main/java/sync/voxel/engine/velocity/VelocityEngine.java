/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.velocity;

import org.bukkit.inventory.ItemStack;
import sync.voxel.engine.api.VoxEngine;
import sync.voxel.engine.api.utils.item.VoxItem;

public class VelocityEngine implements VoxEngine {

    @Override
    public VoxItem voxItemTunnel(ItemStack stack) {
        throw new RuntimeException("Ups looking like voxItemTunnel(org.bukkit.inventory.ItemStack) is not for Velocity");
    }

}
