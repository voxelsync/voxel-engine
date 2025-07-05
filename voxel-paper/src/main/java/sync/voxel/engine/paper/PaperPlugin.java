/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.paper;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import sync.voxel.engine.api.VoxelEngine;
import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.api.common.VoxRenderType;
import sync.voxel.engine.paper.builder.vaconverter.VanillaConverter;
import sync.voxel.engine.paper.runtime.behavior.BlockBehavior;
import sync.voxel.engine.paper.runtime.behavior.ItemBehavior;
import sync.voxel.engine.paper.runtime.command.MainCommand;
import sync.voxel.engine.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.engine.paper.runtime.material.VoxelMaterial;
import sync.voxel.engine.paper.utils.text.Text;

public class PaperPlugin extends JavaPlugin {

    public static PaperPlugin plugin;
    public static Component prefix = Component.text("V").color(TextColor.color(0xff0241)).append(Component.text("E").color(TextColor.color(0x00244f)).append(Component.text(" »").color(TextColor.color(0x555555)))) ;


    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PaperPlugin.plugin = this;
        VoxelEngine.register(new PaperEngine());
        PacketEvents.getAPI().init();
        VanillaConverter.convert();
        BlockBehavior.register();
        Text.registerLangDirectory("https://voxelsync.github.io/translation/");

        PacketEvents.getAPI().getEventManager().registerListener(new ItemBehavior(), PacketListenerPriority.NORMAL);

        VoxelEnchantment.forkEnchantment(VoxKey.of("voxel:vein_ming"));
        VoxelMaterial.forkMaterial(Material.STONE, VoxKey.of("voxel:test_block"), VoxRenderType.BLOCK_TEXTURE_ID);

        MainCommand mainCommand = new MainCommand();
        mainCommand.registerSubCommands();
        getCommand("voxelengine").setExecutor(mainCommand);
        getCommand("voxelengine").setTabCompleter(mainCommand);
    }

    @Override
    public void onDisable() {
        VoxelEngine.unregister();
        PacketEvents.getAPI().terminate();
    }

}
