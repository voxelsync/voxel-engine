package sync.voxel.paper.runtime.listener;

import io.papermc.paper.event.player.PlayerPurchaseEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import sync.voxel.api.runtime.item.VoCreateReason;
import sync.voxel.api.runtime.item.VoItemStack;

public class ItemCreateListener implements Listener {

    // === General Block Events ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getItemInHand(), VoCreateReason.CUSTOM);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        if (!event.isCancelled()) {
            // Track the tool used
            trackItem(event.getPlayer().getInventory().getItemInMainHand(), VoCreateReason.BLOCK_BREAK);
        }
    }

    // === Crafting & Recipes ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCrafterCraft(@NotNull CrafterCraftEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getResult(), VoCreateReason.CRAFTING);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCraftItem(@NotNull CraftItemEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getCurrentItem(), VoCreateReason.CRAFTING);
            // Track recipe ingredients being consumed
            for (ItemStack ingredient : event.getInventory().getMatrix()) {
                trackItem(ingredient, VoCreateReason.CRAFTING_INGREDIENT);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepareCraft(@NotNull PrepareItemCraftEvent event) {
        trackItem(event.getInventory().getResult(), VoCreateReason.PREPARE_CRAFT);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (event.isCancelled()) return;

        // Stonecutter
        if (event.getInventory().getType() == InventoryType.STONECUTTER) {
            trackItem(event.getCurrentItem(), VoCreateReason.STONECUTTING);
        }
        // Cartography
        else if (event.getInventory().getType() == InventoryType.CARTOGRAPHY) {
            trackItem(event.getCurrentItem(), VoCreateReason.CARTOGRAPHY);
        }
        // Loom
        else if (event.getInventory().getType() == InventoryType.LOOM) {
            trackItem(event.getCurrentItem(), VoCreateReason.LOOM);
        }
        // Grindstone
        else if (event.getInventory().getType() == InventoryType.GRINDSTONE) {
            trackItem(event.getCurrentItem(), VoCreateReason.GRINDSTONE);
        }
        // General inventory interactions
        else {
            if (event.getClick().isShiftClick()) {
                trackItem(event.getCurrentItem(), VoCreateReason.INVENTORY_DRAG);
            } else if (event.getClick().isKeyboardClick()) {
                trackItem(event.getCurrentItem(), VoCreateReason.HOTBAR_SWAP);
            } else if (event.getClick().isRightClick() && event.getCursor() != null) {
                trackItem(event.getCursor(), VoCreateReason.STACK_SPLIT);
            }

            // Track any item movement
            trackItem(event.getCurrentItem(), VoCreateReason.INVENTORY_MOVE);
            trackItem(event.getCursor(), VoCreateReason.CURSOR_ITEM);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getOldCursor(), VoCreateReason.INVENTORY_DRAG);
            event.getNewItems().values().forEach(item -> trackItem(item, VoCreateReason.INVENTORY_DRAG));
        }
    }

    // === Smithing ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onSmithing(@NotNull PrepareSmithingEvent event) {
        ItemStack result = event.getResult();
        if (result != null) {
            if (result.getType().toString().contains("TRIM")) {
                trackItem(result, VoCreateReason.ARMOR_TRIM);
            } else {
                trackItem(result, VoCreateReason.SMITHING);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSmithingResult(@NotNull SmithItemEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getCurrentItem(), VoCreateReason.SMITHING);
        }
    }

    // === Brewing ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBrew(@NotNull BrewEvent event) {
        if (!event.isCancelled()) {
            event.getResults().forEach(item -> trackItem(item, VoCreateReason.POTION_MIXING));
        }
    }

    // === Furnace & Smelting ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFurnaceSmelt(@NotNull FurnaceSmeltEvent event) {
        if (!event.isCancelled()) {
            // Determine furnace type based on block
            Block furnace = event.getBlock();
            if (furnace.getType() == Material.BLAST_FURNACE) {
                trackItem(event.getResult(), VoCreateReason.BLASTING);
            } else if (furnace.getType() == Material.SMOKER) {
                trackItem(event.getResult(), VoCreateReason.SMOKING);
            } else {
                trackItem(event.getResult(), VoCreateReason.SMELTING);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCampfireCook(@NotNull CampfireStartEvent event) {
        trackItem(event.getSource(), VoCreateReason.CAMPFIRE_COOK);
    }

    // === Enchanting & Repair ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(@NotNull EnchantItemEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getItem(), VoCreateReason.ENCHANTING);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnvil(@NotNull PrepareAnvilEvent event) {
        trackItem(event.getResult(), VoCreateReason.ANVIL);
    }

    // === Trading & Bartering ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onTrade(@NotNull PlayerPurchaseEvent event) {
        trackItem(event.getTrade().getResult(), VoCreateReason.TRADE);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBarter(@NotNull EntityDropItemEvent event) {
        if (event.getEntityType() == EntityType.PIGLIN) {
            trackItem(event.getItemDrop().getItemStack(), VoCreateReason.BARTERING);
        }
    }

    // === Drops & Loot ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDrop(@NotNull BlockDropItemEvent event) {
        if (!event.isCancelled()) {
            Block block = event.getBlock();
            if (block.getType().toString().contains("SUSPICIOUS")) {
                event.getItems().forEach(item -> trackItem(item.getItemStack(), VoCreateReason.ARCHAEOLOGY));
            } else {
                event.getItems().forEach(item -> trackItem(item.getItemStack(), VoCreateReason.BLOCK_DROP));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(@NotNull EntityDeathEvent event) {
        if (!event.isCancelled()) {
            if (event.getEntity().getType() == EntityType.CREEPER &&
                    event.getEntity().getLastDamageCause() != null &&
                    event.getEntity().getLastDamageCause().getEntity() != null &&
                    event.getEntity().getLastDamageCause().getEntity().getType() == EntityType.SKELETON) {

                event.getDrops().forEach(item -> {
                    if (item.getType().toString().contains("MUSIC_DISC") || item.getType().toString().endsWith("_RECORD")) {
                        trackItem(item, VoCreateReason.RECORD_DROP);
                    } else {
                        trackItem(item, VoCreateReason.ENTITY_DROP);
                    }
                });
            } else {
                event.getDrops().forEach(item -> trackItem(item, VoCreateReason.ENTITY_DROP));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDrop(@NotNull PlayerDropItemEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getItemDrop().getItemStack(), VoCreateReason.PLAYER_DROP);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemSpawn(@NotNull ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();

        // Check spawn reason and location context
        Block block = event.getLocation().getBlock();
        if (block.getType().toString().contains("TRIAL_SPAWNER")) {
            trackItem(itemStack, VoCreateReason.TRIAL_SPAWNER);
        } else if (itemStack.getType().toString().contains("TRIAL_KEY")) {
            trackItem(itemStack, VoCreateReason.TRIAL_KEY);
        } else {
            trackItem(itemStack, VoCreateReason.ITEM_SPAWN);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGoatHorn(@NotNull EntityDropItemEvent event) {
        if (event.getEntityType() == EntityType.GOAT) {
            ItemStack item = event.getItemDrop().getItemStack();
            if (item.getType().toString().contains("GOAT_HORN")) {
                trackItem(item, VoCreateReason.GOAT_HORN);
            }
        }
    }

    // === Fishing ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(@NotNull PlayerFishEvent event) {
        if (!event.isCancelled() && event.getCaught() instanceof Item) {
            trackItem(((Item) event.getCaught()).getItemStack(), VoCreateReason.FISHING);
        }
    }

    // === Loot Generation ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLootGenerate(@NotNull LootGenerateEvent event) {
        event.getLoot().forEach(item -> {
            switch (event.getLootTable().getKey().getKey()) {
                case "chests":
                case "chest":
                    trackItem(item, VoCreateReason.LOOT_CHEST);
                    break;
                case "archaeology":
                    trackItem(item, VoCreateReason.ARCHAEOLOGY);
                    break;
                default:
                    trackItem(item, VoCreateReason.LOOT_TABLE);
                    break;
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        // Track naturally generated loot when containers are opened for the first time
        if (event.getInventory().getType() == InventoryType.CHEST ||
                event.getInventory().getType() == InventoryType.BARREL ||
                event.getInventory().getType() == InventoryType.SHULKER_BOX) {

            for (ItemStack item : event.getInventory().getContents()) {
                if (item != null && !item.getType().isAir()) {
                    trackItem(item, VoCreateReason.LOOT_CHEST);
                }
            }
        }
    }

    // === Item Pickup ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickupItem(@NotNull EntityPickupItemEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getItem().getItemStack(), VoCreateReason.PICKUP);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(@NotNull EntityPickupItemEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getItem().getItemStack(), VoCreateReason.PICKUP);
        }
    }

    // === Creative & Commands ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        if (message.startsWith("/give") || message.startsWith("/item")) {
            // For demonstration - would need proper parsing for actual items
            trackItem(new ItemStack(Material.STONE), VoCreateReason.COMMAND);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreativeGive(@NotNull InventoryCreativeEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getCursor(), VoCreateReason.CREATIVE_GIVE);
            trackItem(event.getCurrentItem(), VoCreateReason.CREATIVE_GIVE);
        }
    }

    // === Player Events ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        // Track starting inventory
        Player player = event.getPlayer();
        for (ItemStack item : player.getInventory().getContents()) {
            trackItem(item, VoCreateReason.PLAYER_JOIN);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(@NotNull PlayerRespawnEvent event) {
        // Track respawn inventory
        Player player = event.getPlayer();
        for (ItemStack item : player.getInventory().getContents()) {
            trackItem(item, VoCreateReason.PLAYER_RESPAWN);
        }
    }

    // === Entity Interactions ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityInteract(@NotNull PlayerInteractEntityEvent event) {
        if (!event.isCancelled()) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            trackItem(item, VoCreateReason.ENTITY_INTERACT);
        }
    }

    // === Bucket Events ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBucketFill(@NotNull PlayerBucketFillEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getItemStack(), VoCreateReason.BUCKET_FILL);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBucketEmpty(@NotNull PlayerBucketEmptyEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getItemStack(), VoCreateReason.BUCKET_EMPTY);
        }
    }

    // === Shearing ===
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerShearEntity(@NotNull PlayerShearEntityEvent event) {
        if (!event.isCancelled()) {
            trackItem(event.getItem(), VoCreateReason.SHEARING);
        }
    }

    // === Utility Method ===
    private void trackItem(ItemStack item, VoCreateReason reason) {
        if (item == null || item.getType().isAir()) {
            return;
        }
        try {
            VoItemStack.of(item, reason);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}