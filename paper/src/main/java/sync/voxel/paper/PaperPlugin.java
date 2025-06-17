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
import sync.voxel.api.VoxelEngine;

public class PaperPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        VoxelEngine.register(new PaperEngine());
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDisable() {
        VoxelEngine.unregister();
    }

}
