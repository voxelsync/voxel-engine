/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api;

import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus.*;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.leycm.storage.StorageSection;
import sync.voxel.api.item.VoItemCreateReason;
import sync.voxel.api.item.VoItemStack;
import sync.voxel.api.material.VoMaterial;
import sync.voxel.api.material.VoRarity;


public final class VoxelEngine {
    private static VoEngine engine = null;

    public static VoMaterial registerVoMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings, VoRarity rarity) {
        return engine.registerVoMaterial(nameSpace, identifier, vaMaterial, settings, rarity);
    }

    public static VoMaterial registerVoMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings) {
        return engine.registerVoMaterial(nameSpace, identifier, vaMaterial, settings);
    }

    public static VoItemStack registerVoItemStack(@NotNull VoMaterial voMaterial, int amount, VoItemCreateReason reason) {
        return engine.registerVoItemStack(voMaterial, amount, reason);
    }

    private static final class NotLoadedException extends IllegalStateException {
        private static final String MESSAGE = """
                The Voxel API isn't loaded yet!
                This could be because:
                 a) the Voxel plugin is not installed or it failed to enable
                 b) the plugin in the stacktrace does not declare a dependency on Voxel
                 c) the plugin in the stacktrace is retrieving the API before the plugin 'enable' phase
                 (call the #get method in onEnable, not the constructor!)
                 d) the plugin in the stacktrace is incorrectly 'shading' the Voxel API into its jar
                """;

        NotLoadedException() {
            super(MESSAGE);
        }
    }

    public static @NonNull VoEngine getEngine() {
        VoEngine engine = VoxelEngine.engine;
        if (engine == null) throw new NotLoadedException();
        return engine;
    }

    @Internal
    public static void register(VoEngine engine) {
        VoxelEngine.engine = engine;
    }

    @Internal
    public static void unregister() {
        VoxelEngine.engine = null;
    }

    @Internal
    private VoxelEngine() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

}
