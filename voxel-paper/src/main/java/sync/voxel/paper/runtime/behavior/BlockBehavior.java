package sync.voxel.paper.runtime.behavior;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import sync.voxel.api.material.VoMaterial;
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
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack stack = event.getItem();
        if (stack == null || stack.getType() != Material.PAPER) {
            return;
        }

        String material = stack.getPersistentDataContainer().get(
                new NamespacedKey("voxelmeta", "material"),
                PersistentDataType.STRING
        );

        if (material != null && VoxelMaterial.valueOf(material).getVaMaterial().isBlock()) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) {
                return;
            }

            Location targetLocation = clickedBlock.getRelative(event.getBlockFace()).getLocation();

            if (targetLocation.getBlock().getType() != Material.AIR) {
                return;
            }

            VoMaterial voxelMat = VoxelMaterial.valueOf(material);
            targetLocation.getBlock().setType(voxelMat.getVaMaterial());

            new VoxelBlock(targetLocation, stack);

            updateVoxelBlockNeighbor(targetLocation, null, 1); // TODO : add radius to config

            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                stack.setAmount(stack.getAmount() - 1);
            }

            event.setCancelled(true);
        }
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
