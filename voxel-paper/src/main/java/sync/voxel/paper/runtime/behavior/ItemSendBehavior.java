package sync.voxel.paper.runtime.behavior;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import sync.voxel.api.enchantment.VoEnchantment;
import sync.voxel.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.paper.utils.item.VoxelItem;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class ItemSendBehavior implements PacketListener {
    private static final Set<Function<VoxelItem, VoxelItem>> editor = new HashSet<>();

    public ItemSendBehavior() {
        editItems(item -> {
            for (VoEnchantment enchantment : VoEnchantment.values()) {
                int level = item.getEnchantLevel(enchantment);
                if (level == 0) continue;

                Component line = Component.text(enchantment.getKey().toString() + " " + VoxelEnchantment.getRomanInteger(level))
                        .color(TextColor.color(0xAAAAAA));
                item.addLoreLine(0, line); // TODO : add translations;
            }

            return item;
        });
    }

    public void editItems(Function<VoxelItem, VoxelItem> edit) {
        editor.add(edit);
    }

    @Override
    public void onPacketSend(@NotNull PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.SET_SLOT) return;
        WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(event);
        ItemStack stack = wrapper.getItem();

        VoxelItem item = VoxelItem.edit(SpigotConversionUtil.toBukkitItemStack(stack));
        editor.forEach(edit -> edit.apply(item));
        SpigotConversionUtil.fromBukkitItemStack(item.stack());

        wrapper.setItem(stack);
    }
}