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
 * An event that is fired when a custom Voxel material is being registered.
 * <p>
 * This event allows plugins to:
 * <ul>
 *   <li>Monitor custom material registrations</li>
 *   <li>Modify material properties before registration</li>
 *   <li>Cancel the registration entirely</li>
 * </ul>
 *
 * <p>The event contains all relevant information about the material being registered,
 * including its namespace, identifier, base Minecraft material, rarity, and custom settings.
 *
 * <p>This event implements {@link Cancellable} and can be cancelled to prevent the
 * material from being registered.
 *
 * @see VoRarity
 * @see org.leycm.storage.StorageSection
 */
@Getter @Setter
public class RegisterVoMaterialEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Whether the event is cancelled
     */
    private boolean cancel;

    /**
     * The namespace of the material (usually the plugin name)
     */
    private final String nameSpace;

    /**
     * The unique identifier for the material within its namespace
     */
    private final String identifier;

    /**
     * The base Minecraft material this custom material is based on
     */
    private final Material vaMaterial;

    /**
     * The rarity of the custom material
     */
    private final VoRarity rarity;

    /**
     * Additional settings and configuration for the material
     */
    private final StorageSection settings;

    /**
     * Creates a new RegisterVoMaterialEvent
     *
     * @param nameSpace The namespace for the material (usually plugin name)
     * @param identifier The unique identifier for the material
     * @param vaMaterial The base Minecraft material
     * @param settings Additional material settings
     * @param rarity The rarity of the material
     */
    public RegisterVoMaterialEvent(String nameSpace, String identifier, Material vaMaterial, StorageSection settings, VoRarity rarity) {
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
     * Gets the handler list for this event
     *
     * @return The handler list
     */
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
