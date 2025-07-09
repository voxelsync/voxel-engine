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
