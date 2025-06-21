/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api.runtime.event.item;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import sync.voxel.api.startup.material.VoMaterial;

/**
 * Called when a VoItemStack is being created.
 * <p>
 * This event allows plugins to modify or cancel the creation of VoItemStack instances.
 * </p>
 */
@Getter @Setter
public class CreateVoItemStackEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private VoMaterial material;
    private int amount;

    public CreateVoItemStackEvent(@NotNull VoMaterial material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    /**
     * Gets the material for the VoItemStack being created.
     *
     * @return the VoMaterial
     */
    @NotNull
    public VoMaterial getMaterial() {
        return material;
    }

    /**
     * Sets the material for the VoItemStack being created.
     *
     * @param material the new VoMaterial
     */
    public void setMaterial(@NotNull VoMaterial material) {
        this.material = material;
    }

    /**
     * Sets the amount for the VoItemStack being created.
     *
     * @param amount the new amount
     */
    public void setAmount(int amount) {
        this.amount = Math.max(1, amount);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the handler list for this event
     *
     * @return The handler list
     */
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}