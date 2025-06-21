/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api.runtime.event.material;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.leycm.storage.StorageSection;
import sync.voxel.api.startup.material.VoRarity;

/**
 * An event that is fired when a Voxel material is being adapted or modified.
 * <p>
 * This event allows plugins to:
 * <ul>
 *   <li>Monitor material adaptation processes</li>
 *   <li>Modify material properties during adaptation</li>
 *   <li>Cancel the adaptation if needed</li>
 * </ul>
 *
 * <p>The event contains all relevant information about the material being adapted,
 * including its namespace, identifier, base Minecraft material, rarity, and custom settings.
 *
 * <p>This event implements {@link Cancellable} and can be cancelled to prevent the
 * material adaptation from occurring.
 *
 * @see VoRarity
 * @see org.leycm.storage.StorageSection
 * @see org.bukkit.event.Cancellable
 */
@Getter @Setter
public class AdaptVaMaterialEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Whether the event is cancelled
     */
    private boolean cancel;

    /**
     * The namespace of the material (typically the plugin name)
     */
    private final String nameSpace;

    /**
     * The unique identifier for the material within its namespace
     */
    private final String identifier;

    /**
     * The base Minecraft material being adapted
     */
    private final Material vaMaterial;

    /**
     * The rarity classification of the material
     */
    private final VoRarity rarity;

    /**
     * Configuration settings for the material adaptation
     */
    private final StorageSection settings;

    /**
     * Constructs a new AdaptVaMaterialEvent
     *
     * @param nameSpace The namespace for the material (usually plugin name)
     * @param identifier The unique identifier for the material
     * @param vaMaterial The base Minecraft material being adapted
     * @param settings Configuration settings for the adaptation
     * @param rarity The rarity classification of the material
     */
    public AdaptVaMaterialEvent(String nameSpace, String identifier, Material vaMaterial, StorageSection settings, VoRarity rarity) {
        this.nameSpace = nameSpace;
        this.identifier = identifier;
        this.vaMaterial = vaMaterial;
        this.settings = settings;
        this.rarity = rarity;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the handler list for this event type
     *
     * @return The handler list for this event
     */
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
