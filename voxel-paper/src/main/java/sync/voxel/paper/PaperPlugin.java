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

import org.bukkit.plugin.java.JavaPlugin;
import org.leycm.storage.Storage;
import org.leycm.storage.impl.JavaStorage;
import sync.voxel.api.VoxelEngine;

public class PaperPlugin extends JavaPlugin {

    public static PaperPlugin plugin;

    @Override
    public void onEnable() {
        PaperPlugin.plugin = this;
        VoxelEngine.register(new PaperEngine());
        Storage.of("empty", Storage.Type.JSON, true, JavaStorage.class).set("empty", "empty");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {
        VoxelEngine.unregister();
    }

}
