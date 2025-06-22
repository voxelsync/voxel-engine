package sync.voxel.paper.runtime.behavior;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import sync.voxel.paper.PaperPlugin;
import sync.voxel.paper.runtime.material.VoxelMaterial;
import sync.voxel.paper.runtime.world.VoxelBlock;
import sync.voxel.paper.runtime.world.VoxelWorld;

public final class BlockBehavior implements Listener {

    private static BlockBehavior blockBehavior;

    public static void register() {
        if (blockBehavior == null) BlockBehavior.blockBehavior = new BlockBehavior();
        Bukkit.getServer().getPluginManager().registerEvents(blockBehavior, PaperPlugin.getPlugin(PaperPlugin.class));
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        updateVoxelBlockNeighbor(event.getBlock().getLocation(), event.getBlock().getLocation(), 1); // TODO : add radius to config
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        ItemStack stack = event.getItemInHand();
        String material =  stack.getPersistentDataContainer().get(new NamespacedKey("voxelmeta", "material"), PersistentDataType.STRING);
        if (material != null && VoxelMaterial.valueOf(material).getVaMaterial().isBlock()) {
            new VoxelBlock(event.getBlock().getLocation(), stack);
        }

        updateVoxelBlockNeighbor(event.getBlock().getLocation(), null, 1); // TODO : add radius to config
    }


    private void updateVoxelBlockNeighbor(@NotNull Location location, Location airLoc, int radius) {
        Location base = location.toBlockLocation();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (dist == 0 || dist > radius) continue;

                    Location neighbor = base.clone().add(x, y, z);
                    if (VoxelWorld.contains(neighbor)) {
                        VoxelWorld.getVoxelBlock(neighbor).updateBlock(airLoc);
                    }

                }
            }
        }

    }


}
