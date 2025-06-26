package sync.voxel.paper.runtime.behavior;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import sync.voxel.api.enchantment.VoEnchantment;
import sync.voxel.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.paper.utils.item.VoxelItem;

import java.util.*;

public class ItemSendBehavior implements PacketListener {

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            handleSetSlot(event);
        } else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            handleWindowItems(event);
        }
    }

    private void handleSetSlot(PacketSendEvent event) {
        WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(event);
        ItemStack packetItem = wrapper.getItem();
        org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packetItem).clone();
        processItem(stack).ifPresent(wrapper::setItem);
    }

    private void handleWindowItems(PacketSendEvent event) {
        WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);
        List<ItemStack> items = wrapper.getItems();
        for (int i = 0; i < items.size(); i++) {
            ItemStack packetItem = items.get(i);
            org.bukkit.inventory.ItemStack stack = SpigotConversionUtil.toBukkitItemStack(packetItem).clone();
            int finalI = i;
            processItem(stack).ifPresent(newStack -> items.set(finalI, newStack));
        }
    }

    private Optional<ItemStack> processItem(org.bukkit.inventory.@NotNull ItemStack stack) {
        if (stack.getType() == Material.AIR || Bukkit.getItemFactory().getItemMeta(stack.getType()) == null) {
            return Optional.empty();
        }

        if (!stack.hasItemMeta()) {
            stack.setItemMeta(Bukkit.getItemFactory().getItemMeta(stack.getType()));
        }

        VoxelItem item = VoxelItem.clone(stack);
        applyName(item);
        applyEnchantments(item);

        return Optional.of(SpigotConversionUtil.fromBukkitItemStack(item.toNewItemStack()));
    }

    private void applyName(@NotNull VoxelItem item){
        String name = item.getVoMaterial().getKey().toString(); // TODO : add translations;
        item.setDisplayName(Component.text(name, TextColor.color(0xFFFFFF)).decoration(TextDecoration.ITALIC, false)); // TODO : add rarity color + custom enchant to enchant name support
    }

    private void applyEnchantments(@NotNull VoxelItem item){
        List<Component> lore = item.getLore();

        List<VoEnchantment> enchantments = new ArrayList<>(VoEnchantment.values().stream().toList());
        Collections.reverse(enchantments);

        for (VoEnchantment enchantment : enchantments) {
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

}