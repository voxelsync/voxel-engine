/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.paper.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.leycm.storage.StorageSection;

import sync.voxel.api.event.material.RegisterVoMaterialEvent;
import sync.voxel.api.item.VoMaterial;
import sync.voxel.api.item.VoRarity;

public class VoxelMaterial implements VoMaterial {
    private final String nameSpace;
    private final String identifier;
    private final Material vaMaterial;
    private final VoRarity rarity;
    private final StorageSection settings;

    public VoxelMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings) {
        this(nameSpace, identifier, vaMaterial, settings, VoRarity.COMMON);
    }

    public VoxelMaterial(String nameSpace, String identifier, Material vaMaterial, StorageSection settings, VoRarity rarity) {
        RegisterVoMaterialEvent event = new RegisterVoMaterialEvent(
                nameSpace, identifier,
                vaMaterial, settings,
                rarity
        );

        Bukkit.getPluginManager().callEvent(event);

        this.nameSpace = event.getNameSpace();
        this.identifier = event.getIdentifier();
        this.vaMaterial = event.getVaMaterial();
        this.settings = event.getSettings();
        this.rarity = event.getRarity();

        if (!event.isCancelled()) {
            materials.add(this);
        }
    }

    @Override
    public String nameSpace() {
        return nameSpace;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public Material vaMaterial() {
        return vaMaterial;
    }

    @Override
    public StorageSection settings() {
        return settings;
    }

    @Override
    public VoRarity rarity() {
        return rarity;
    }

    @Override
    public boolean isAir() {
        return vaMaterial() == Material.AIR || vaMaterial() == Material.CAVE_AIR || vaMaterial() == Material.VOID_AIR;
    }

    @Override
    public boolean isBlock() {
        return settings.get("type", String.class, vaMaterial.isBlock() ? "block" : "air").equals("block");
    }

    @Override
    public boolean isSolidBlock() {
        return isBlock() && settings.get("block.solid", Boolean.class, vaMaterial.isSolid());
    }

    @Override
    public boolean isOccluding() {
        return isBlock() && settings.get("block.occluding", Boolean.class, vaMaterial.isOccluding());
    }

    @Override
    public boolean isEdible() {
        return settings.get("edible", Boolean.class, vaMaterial.isEdible());
    }

    @Override
    public boolean isFlammable() {
        return settings.get("flammable", Boolean.class, vaMaterial.isFlammable());
    }

    @Override
    public boolean isBurnable() {
        return settings.get("burnable", Boolean.class, vaMaterial.isBurnable());
    }

    @Override
    public boolean isFuel() {
        return settings.get("fuel", Boolean.class, vaMaterial.isFuel());
    }

    @Override
    public boolean isInteractable() {
        return settings.get("interactable", Boolean.class, vaMaterial.isInteractable());
    }

    @Override
    public boolean isRecord() {
        return settings.get("record", Boolean.class, vaMaterial.isRecord());
    }

    @Override
    public boolean hasGravity() {
        return settings.get("gravity", Boolean.class, vaMaterial.hasGravity());
    }

    @Override
    public float getHardness() {
        return settings.get("hardness", Float.class, vaMaterial.getHardness());
    }

    @Override
    public float getBlastResistance() {
        return settings.get("blastResistance", Float.class, vaMaterial.getBlastResistance());
    }

    @Override
    public int getMaxStackSize() {
        return settings.get("maxStackSize", Integer.class, vaMaterial.getMaxStackSize());
    }

    @Override
    public short getMaxDurability() {
        return settings.get("maxDurability", Short.class, vaMaterial.getMaxDurability());
    }

    @Override
    public boolean isItem() {
        return settings.get("item", Boolean.class, vaMaterial.isItem());
    }

    @Override
    public boolean isLegacy() {
        return settings.get("legacy", Boolean.class, vaMaterial.isLegacy());
    }

    @Override
    public String getKey() {
        return settings.get("key", String.class, vaMaterial.getKey().getKey());
    }

    @Override
    public String name() {
        return settings.get("name", String.class, vaMaterial.name());
    }

    @Override
    public int ordinal() {
        return settings.get("ordinal", Integer.class, vaMaterial.ordinal());
    }

    @Override
    public boolean isCollidable() {
        return settings.get("collidable", Boolean.class, vaMaterial.isCollidable());
    }

    @Override
    public float getSlipperiness() {
        return settings.get("slipperiness", Float.class, vaMaterial.getSlipperiness());
    }

    @Override
    public boolean isSolid() {
        return settings.get("solid", Boolean.class, vaMaterial.isSolid());
    }

    @Override
    public <T> T getSetting(String key, Class<T> type, T defaultValue) {
        return settings.get(key, type, defaultValue);
    }

    @Override
    public String toString() {
        return nameSpace + ":" + identifier;
    }

}