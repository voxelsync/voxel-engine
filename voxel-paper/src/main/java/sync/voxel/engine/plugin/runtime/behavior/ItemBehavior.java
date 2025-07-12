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

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import sync.voxel.engine.api.enchantment.VoxEnchantment;
import sync.voxel.engine.plugin.utils.enchantment.VoxelEnchantment;
import sync.voxel.engine.plugin.utils.item.VoxelItem;

import java.util.*;

public final class ItemBehavior implements PacketListener {

    private static ItemBehavior instance;

    private ItemBehavior() {}

    public static void register() {
        if (instance == null) {
            instance = new ItemBehavior();
            PacketEvents.getAPI().getEventManager().registerListener(instance, PacketListenerPriority.NORMAL);
        }
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            handleSetSlot(event);
        } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            handleWindowItems(event);
        }
    }

    @Override
    public void onPacketReceive(@NotNull PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            handleClickWindow(event);
        } else if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            handleCreativeInventoryAction(event);
        }
    }

    private void handleSetSlot(PacketSendEvent event) {
        WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(event);
        ItemStack packetItem = wrapper.getItem();
        org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packetItem).clone();
        processItemToClient(stack, event.getPlayer()).ifPresent(wrapper::setItem);
    }

    private void handleWindowItems(PacketSendEvent event) {
        WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);
        List<ItemStack> items = wrapper.getItems();
        for (int i = 0; i < items.size(); i++) {
            ItemStack packetItem = items.get(i);
            org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packetItem).clone();
            int finalI = i;
            processItemToClient(stack, event.getPlayer()).ifPresent(newStack -> items.set(finalI, newStack));
        }
    }

    private void handleClickWindow(PacketReceiveEvent event) {
        WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);
        ItemStack carriedItem = wrapper.getCarriedItemStack();
        org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(carriedItem).clone();
        processItemToServer(stack, event.getPlayer()).ifPresent(newStack ->
                wrapper.setCarriedItemStack(SpigotConversionUtil.fromBukkitItemStack(newStack)));
    }

    private void handleCreativeInventoryAction(PacketReceiveEvent event) {
        WrapperPlayClientCreativeInventoryAction wrapper = new WrapperPlayClientCreativeInventoryAction(event);
        ItemStack clickedItem = wrapper.getItemStack();
        org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(clickedItem).clone();
        processItemToServer(stack, event.getPlayer()).ifPresent(newStack ->
                wrapper.setItemStack(SpigotConversionUtil.fromBukkitItemStack(newStack)));
    }

    private Optional<ItemStack> processItemToClient(org.bukkit.inventory.@NotNull ItemStack stack, Player player) {
        if (stack.getType() == Material.AIR || Bukkit.getItemFactory().getItemMeta(stack.getType()) == null) {
            return Optional.empty();
        }

        if (!stack.hasItemMeta()) {
            stack.setItemMeta(Bukkit.getItemFactory().getItemMeta(stack.getType()));
        }

        VoxelItem item = VoxelItem.clone(stack);
        applyName(item, player);
        applyEnchantments(item);

        return Optional.of(SpigotConversionUtil.fromBukkitItemStack(item.toNewItemStack()));
    }

    private Optional<org.bukkit.inventory.ItemStack> processItemToServer(org.bukkit.inventory.@NotNull ItemStack stack, Player player) {
        if (stack.getType() == Material.AIR || !stack.hasItemMeta()) {
            return Optional.empty();
        }

        VoxelItem item = VoxelItem.clone(stack);

        if (!isModifiedByUs(item)) {
            return Optional.empty();
        }

        revertName(item);
        revertEnchantments(item);

        return Optional.of(item.toNewItemStack());
    }

    private boolean isModifiedByUs(@NotNull VoxelItem item) {
        if (item.getDisplayName() != null) {
            String displayName = PlainTextComponentSerializer.plainText().serialize(item.getDisplayName());
            String materialName = item.getVoMaterial().getKey().toString();
            if (displayName.equals(materialName)) {
                return true;
            }
        }

        List<Component> lore = item.getLore();
        if (lore != null && !lore.isEmpty()) {
            for (VoxEnchantment enchantment : VoxEnchantment.values()) {
                String enchantName = enchantment.getKey().toString();
                for (Component line : lore) {
                    String lineText = PlainTextComponentSerializer.plainText().serialize(line);
                    if (lineText.startsWith(enchantName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void applyName(@NotNull VoxelItem item, Player player){
        item.setPersistentData("voxel-meta", "has-modified-name", PersistentDataType.BOOLEAN, false);
        if (item.getDisplayName() != null) return;

        String name = item.getVoMaterial().getNameFor(player); // TODO : add translations;
        item.setPersistentData("voxel-meta", "has-modified-name", PersistentDataType.BOOLEAN, true);
        item.setDisplayName(Component.text(name, TextColor.color(0xFFFFFF)).decoration(TextDecoration.ITALIC, false)); // TODO : add rarity color + custom enchant to enchant name support
    }

    private void applyEnchantments(@NotNull VoxelItem item){
        List<Component> lore = item.getLore();

        List<VoxEnchantment> enchantments = new ArrayList<>(VoxEnchantment.values().stream().toList());
        Collections.reverse(enchantments);

        for (VoxEnchantment enchantment : enchantments) {
            int level = item.getEnchantLevel(enchantment);
            if (level == 0) continue;

            String name = enchantment.getKey().toString(); // TODO : add translations;

            Component line = Component.text(name + " " + VoxelEnchantment.getRomanInteger(level))
                    .color(TextColor.color(0xAAAAAA)).decoration(TextDecoration.ITALIC, false);

            lore.remove(line);
            lore.removeIf(ifLine -> PlainTextComponentSerializer.plainText().serialize(ifLine).startsWith(name));
            lore.addFirst(line);
            item.stack().editMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_STORED_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES));
        }

        item.setLore(lore);
    }

    private void revertName(@NotNull VoxelItem item) {
        if (item.getDisplayName() == null) return;

        if (Boolean.TRUE.equals(item.getPersistentData("voxel-meta", "has-modified-name", PersistentDataType.BOOLEAN))) {
            item.setDisplayName(null);
        }

    }

    private void revertEnchantments(@NotNull VoxelItem item) {
        List<Component> lore = item.getLore();
        if (lore == null || lore.isEmpty()) return;

        List<Component> newLore = new ArrayList<>(lore);

        for (VoxEnchantment enchantment : VoxEnchantment.values()) {
            String enchantName = enchantment.getKey().toString();
            newLore.removeIf(line -> {
                String lineText = PlainTextComponentSerializer.plainText().serialize(line);
                return lineText.startsWith(enchantName + " ");
            });
        }

        if (newLore.isEmpty()) {
            item.setLore(null);
        } else {
            item.setLore(newLore);
        }

        ItemMeta meta = item.stack().getItemMeta();
        if (meta != null) {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_STORED_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            item.stack().setItemMeta(meta);
        }
    }
}