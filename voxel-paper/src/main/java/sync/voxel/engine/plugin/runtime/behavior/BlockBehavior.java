/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.plugin.runtime.behavior;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import org.jetbrains.annotations.NotNull;

import sync.voxel.engine.api.material.VoxMaterial;
import sync.voxel.engine.plugin.PaperPlugin;
import sync.voxel.engine.plugin.runtime.world.VoxelBlock;
import sync.voxel.engine.plugin.runtime.world.VoxelWorld;
import sync.voxel.engine.plugin.common.item.VoxelItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BlockBehavior implements Listener {

    private static BlockBehavior instance;
    private static final Map<UUID, Long> placementCooldowns = new HashMap<>();
    private static final long COOLDOWN = 100;

    public static void register() {
        if (instance == null) {
            BlockBehavior.instance = new BlockBehavior();
            Bukkit.getServer().getPluginManager().registerEvents(instance, PaperPlugin.getPlugin(PaperPlugin.class));
        }
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        updateVoxelBlockNeighbor(event.getBlock().getLocation(), event.getBlock().getLocation(), 1); // TODO : add radius to config
        if (VoxelWorld.containsVoxelBlock(event.getBlock().getLocation())) {
            int dropChance = 1;

            if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) dropChance = 0;

            VoxelWorld.getVoxelBlock(event.getBlock().getLocation()).breakBlock(dropChance, event.getPlayer());
            event.setDropItems(false);
        }
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        updateVoxelBlockNeighbor(event.getBlock().getLocation(), null, 1); // TODO : add radius to config
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack stack = event.getItem();
        if (stack == null || stack.getType() != Material.PAPER) return;

        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastUsage = placementCooldowns.get(playerId);

        if (lastUsage != null && (currentTime - lastUsage) < COOLDOWN) {
            event.setCancelled(true);
            return;
        }

        VoxMaterial voxelMaterial = VoxelItem.edit(stack).getVoMaterial();

        if (!voxelMaterial.getVaMaterial().isBlock()) return;

        Location targetLocation = clickedBlock.getRelative(event.getBlockFace()).getLocation();
        Block targetBlock = targetLocation.getBlock();

        if (!canPlaceBlock(player, targetBlock, clickedBlock)) {
            event.setCancelled(false);
            return;
        }

        targetBlock.setType(voxelMaterial.getVaMaterial());
        new VoxelBlock(targetLocation, stack);

        updateVoxelBlockNeighbor(targetLocation.toBlockLocation(), null, 1); // TODO : get Radius from Config

        if (player.getGameMode() != GameMode.CREATIVE) {
            stack.setAmount(stack.getAmount() - 1);
        }

        placementCooldowns.put(playerId, currentTime);
        event.setCancelled(true);
    }

    private boolean canPlaceBlock(Player player, @NotNull Block targetBlock, Block clickedBlock) {
        if (targetBlock.getType() != Material.AIR) return false;

        if (!isBlockClear(targetBlock)) return false;

        Material clickedType = clickedBlock.getType();
        return player.isSneaking() || (
                clickedType != Material.CHEST &&
                        clickedType != Material.TRAPPED_CHEST &&
                        clickedType != Material.ENDER_CHEST &&
                        clickedType != Material.FURNACE &&
                        clickedType != Material.BLAST_FURNACE &&
                        clickedType != Material.SMOKER &&
                        clickedType != Material.BARREL &&
                        clickedType != Material.BREWING_STAND &&
                        clickedType != Material.CRAFTING_TABLE &&
                        clickedType != Material.ANVIL &&
                        clickedType != Material.CHIPPED_ANVIL &&
                        clickedType != Material.DAMAGED_ANVIL &&
                        clickedType != Material.GRINDSTONE &&
                        clickedType != Material.LECTERN &&
                        clickedType != Material.LOOM &&
                        clickedType != Material.CARTOGRAPHY_TABLE &&
                        clickedType != Material.FLETCHING_TABLE &&
                        clickedType != Material.SMITHING_TABLE &&
                        clickedType != Material.STONECUTTER &&

                        !clickedType.toString().endsWith("_DOOR") &&
                        !clickedType.toString().endsWith("_TRAPDOOR") &&
                        !clickedType.toString().endsWith("_FENCE_GATE") &&
                        clickedType != Material.IRON_DOOR &&
                        clickedType != Material.IRON_TRAPDOOR &&

                        !clickedType.toString().endsWith("_BUTTON") &&
                        clickedType != Material.LEVER &&

                        !clickedType.toString().endsWith("_SIGN") &&
                        !clickedType.toString().endsWith("_WALL_SIGN") &&
                        !clickedType.toString().endsWith("_HANGING_SIGN") &&
                        !clickedType.toString().endsWith("_WALL_HANGING_SIGN") &&

                        !clickedType.toString().endsWith("_BED") &&

                        !clickedType.toString().endsWith("_SHULKER_BOX") &&

                        clickedType != Material.NOTE_BLOCK &&
                        clickedType != Material.JUKEBOX &&
                        clickedType != Material.BELL &&
                        clickedType != Material.COMPOSTER &&
                        clickedType != Material.FLOWER_POT &&
                        clickedType != Material.END_PORTAL_FRAME &&
                        clickedType != Material.DAYLIGHT_DETECTOR &&
                        clickedType != Material.CAULDRON &&
                        clickedType != Material.CONDUIT &&
                        clickedType != Material.ENCHANTING_TABLE &&
                        clickedType != Material.BEACON &&
                        clickedType != Material.REPEATER &&
                        clickedType != Material.COMPARATOR &&
                        !clickedType.toString().endsWith("_BANNER") &&
                        clickedType != Material.SOUL_CAMPFIRE &&
                        clickedType != Material.CAMPFIRE
        );
    }

    public boolean isBlockClear(Block targetBlock) {
        BoundingBox blockBox = BoundingBox.of(targetBlock);

        for (Entity entity : targetBlock.getWorld().getNearbyEntities(blockBox)) {
            if (entity instanceof LivingEntity livingEntity) {
                BoundingBox entityBox = livingEntity.getBoundingBox();
                if (entityBox.overlaps(blockBox)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateVoxelBlockNeighbor(@NotNull Location location, Location airLoc, int radius) {
        Location base = location.toBlockLocation();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    int dist = Math.abs(x) + Math.abs(y) + Math.abs(z);
                    if (dist == 0 || dist > radius) continue;

                    Location neighbor = base.clone().add(x, y, z);
                    if (VoxelWorld.containsVoxelBlock(neighbor)) {
                        VoxelWorld.getVoxelBlock(neighbor).updateBlock(airLoc);
                    }

                }
            }
        }

    }


}
