/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.plugin;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import sync.voxel.engine.api.VoxelEngine;
import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.api.common.VoxRenderType;
import sync.voxel.engine.plugin.builder.vaconverter.VanillaConverter;
import sync.voxel.engine.plugin.common.logger.VoxLogger;
import sync.voxel.engine.plugin.runtime.behavior.BlockBehavior;
import sync.voxel.engine.plugin.runtime.behavior.EnchantmentBehavior;
import sync.voxel.engine.plugin.runtime.behavior.ItemBehavior;
import sync.voxel.engine.plugin.runtime.command.MainCommand;
import sync.voxel.engine.plugin.common.registry.enchantment.VoxelEnchantment;
import sync.voxel.engine.plugin.common.registry.material.VoxelMaterial;
import sync.voxel.engine.plugin.utils.text.Text;

import java.util.logging.Logger;

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
        EnchantmentBehavior.register();
        ItemBehavior.register();

        Text.registerLangDirectory("https://voxelsync.github.io/translation/");

        VoxelEnchantment.forkEnchantment(VoxKey.of("voxel:vein_ming"));
        VoxelMaterial.forkMaterial(Material.STONE, VoxKey.of("voxel:test_block"), VoxRenderType.BLOCK_TEXTURE_ID);

        for (char c = 'A'; c <= 'Z'; c++) {
            String s = String.valueOf(c);
            VoxLogger logger = VoxLogger.registerLogger(s.toLowerCase());
            logger.setIcon(c, 0x005BB7);
            logger.announce(Component.text("Hallo das ist \""+ c + "\" lol"));
            logger.info("info");
            logger.debug("debug");
            logger.warn("warn");
        }



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
