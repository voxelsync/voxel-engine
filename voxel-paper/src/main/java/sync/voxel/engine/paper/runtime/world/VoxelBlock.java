package sync.voxel.engine.paper.runtime.world;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.Arrays;

@Getter @Setter
public class VoxelBlock {
    private Location location;
    private final ItemDisplay display; // use item for custom model data
    private final ItemStack stack;
    private float scale = 1.001f;
    private int[] offset = new int[]{0, 0, 0};

    public VoxelBlock(@NotNull Location location, @NotNull ItemStack stack) {
        location.setPitch(0);
        location.setYaw(0);

        this.location = location;
        this.offset = getBestOffset(null);
        this.stack = stack.clone();
        this.stack.setAmount(1);
        this.display = location.getWorld().spawn(location, ItemDisplay.class, i -> updateDisplay(i, null));

        VoxelWorld.addVoxelBlock(location, this);
    }

    public void updateBlock(Location airLoc) {
        updateDisplay(display, airLoc);
    }

    public void updateDisplay(@NotNull ItemDisplay display, Location airLoc) {
        offset = getBestOffset(airLoc);
        scale = Arrays.equals(offset, new int[]{0, 0, 0}) ? 0.999f : 1.001f;

        display.setItemStack(stack);
        display.teleport(location.clone().add(offset[0], offset[1], offset[2]), PlayerTeleportEvent.TeleportCause.EXIT_BED);

        display.setTransformation(new Transformation(
                new Vector3f(-offset[0] + 0.5f, -offset[1] + 0.5f, -offset[2] + 0.5f),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(scale, scale, scale),
                new Quaternionf(0, 0, 0, 1)
        ));
    }

    public void breakBlock(double chancePercent) {
        VoxelWorld.removeVoxelBlock(this);

        display.remove();

        if (new Random().nextFloat() <= chancePercent) {
            Item dropped = location.getWorld().dropItem(location.add(0.5, 0.5, 0.5), stack);
            dropped.setVelocity(new Vector(0, 0.2, 0));
        }
    }

    public void moveBlock(@NotNull Location location) {
        location.setPitch(90F);
        this.location = location;
        updateBlock(null);
    }

    private int @NotNull [] getBestOffset(Location airLoc) {
        Location base = location.toBlockLocation();
        int[][] offsets = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1},
                {-1, 0, 0}, {0, -1, 0}, {0, 0, -1}};

        for (int[] offset : offsets) {
            Location neighbor = base.clone().add(offset[0], offset[1], offset[2]);
            if (airLoc != null && airLoc.equals(neighbor)) return offset;
            if (!neighbor.getWorld().getBlockAt(neighbor).getType().isOccluding()) return offset;
        }

        return new int[]{0, 0, 0};
    }

}
