package sync.voxel.paper.runtime.world;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VoxelWorld {
    private static Map<Location, VoxelBlock> voxelBlocks = new HashMap<>();

    public static VoxelBlock getVoxelBlock(Location location) {
        return voxelBlocks.get(location);
    }

    public static boolean containsVoxelBlock(Location location) {
        return voxelBlocks.containsKey(location);
    }

    public static VoxelBlock removeVoxelBlock(Location location) {
        return voxelBlocks.remove(location);
    }

    public static VoxelBlock removeVoxelBlock(@NotNull VoxelBlock block) {
        return voxelBlocks.remove(block.getLocation());
    }

    public static void addVoxelBlock(Location location, VoxelBlock block) {
        voxelBlocks.put(location, block);
    }

}
