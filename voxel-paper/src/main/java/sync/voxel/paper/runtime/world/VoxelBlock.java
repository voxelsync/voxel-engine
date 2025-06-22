package sync.voxel.paper.runtime.world;

import io.papermc.paper.entity.TeleportFlag;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import sync.voxel.api.material.VoMaterial;

@Getter @Setter
public class VoxelBlock {
    private final Location location;
    private final ItemDisplay display; // use item for custom model data
    private final ItemStack stack;
    private int[] offset = new int[]{0, 0, 0};

    public VoxelBlock(@NotNull Location location, ItemStack stack) {
        this.location = location;
        this.stack = stack;
        this.display = location.getWorld().spawn(location, ItemDisplay.class, i -> updateBlock(location));
    }

    public void updateBlock(Location updateLoc) {
        offset = getBestOffset();
        display.setItemStack(stack);
        display.teleport(location.clone().add(offset[0], offset[1], offset[2]), PlayerTeleportEvent.TeleportCause.EXIT_BED);
        display.setTransformation(new Transformation(
                new Vector3f(-offset[0], -offset[1], -offset[2]),
                new Quaternionf(),
                new Vector3f(1, 1, 1),
                new Quaternionf()
        ));
    }

    public void breakBlock() {

    }

    private int @NotNull [] getBestOffset() {
        Location base = location.toBlockLocation();
        int[][] offsets = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1},
                {-1, 0, 0}, {0, -1, 0}, {0, 0, -1}};

        for (int[] offset : offsets) {
            Location neighbor = base.clone().add(offset[0], offset[1], offset[2]);
            if (!neighbor.getWorld().getBlockAt(neighbor).getType().isSolid()) return offset;
        }

        return new int[]{0, 0, 0};
    }

}
