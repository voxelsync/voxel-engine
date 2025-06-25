/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.paper;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import sync.voxel.api.VoxelEngine;
import sync.voxel.api.common.VoKey;
import sync.voxel.api.common.VoRenderType;
import sync.voxel.paper.builder.vaconverter.VanillaConverter;
import sync.voxel.paper.runtime.behavior.BlockBehavior;
import sync.voxel.paper.runtime.command.MainCommand;
import sync.voxel.paper.runtime.enchantment.VoxelEnchantment;
import sync.voxel.paper.runtime.material.VoxelMaterial;

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


        VoxelEnchantment.forkEnchantment(new VoKey("voxel", "vein_ming"));
        VoxelMaterial.forkMaterial(Material.STONE, VoKey.of("voxel:test_block"), VoRenderType.BLOCK_TEXTURE_ID);

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

    @Contract(pure = true)
    public static @Nullable Integer getInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null; // Falls die Eingabe ungültig ist
        }
    }

}
