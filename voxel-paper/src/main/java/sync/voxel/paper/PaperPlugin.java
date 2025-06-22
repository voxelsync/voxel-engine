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

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.leycm.storage.Storage;
import org.leycm.storage.impl.JavaStorage;
import sync.voxel.api.VoxelEngine;
import sync.voxel.api.common.VoKey;
import sync.voxel.api.common.VoRenderType;
import sync.voxel.paper.builder.vaconverter.VanillaConverter;
import sync.voxel.paper.runtime.behavior.BlockBehavior;
import sync.voxel.paper.runtime.command.TestCommand;
import sync.voxel.paper.runtime.material.VoxelMaterial;

public class PaperPlugin extends JavaPlugin {

    public static PaperPlugin plugin;

    @Override
    public void onEnable() {
        PaperPlugin.plugin = this;
        VoxelEngine.register(new PaperEngine());
        VanillaConverter.convert();

        BlockBehavior.register();
        VoxelMaterial.forkMaterial(Material.STONE, VoKey.of("voxel:test_block"), VoRenderType.BLOCK_TEXTURE_ID);

        getCommand("test").setExecutor(new TestCommand());
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {
        VoxelEngine.unregister();
    }

}
