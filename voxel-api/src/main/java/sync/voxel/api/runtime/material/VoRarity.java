/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api.runtime.material;

import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;

public enum VoRarity {
    COMMON(0xFFFFFF, "Common"),
    UNCOMMON(0xFFFF55, "Uncommon"),
    RARE(0x55FFFF, "Rare"),
    EPIC(0xFF55FF, "Epic"),
    LEGENDARY(0xFFAA00, "Legendary"),
    MYTHIC(0xFF5555, "Mythic"),
    CREATIVE(0xFF00000, "Creative");

    @Getter
    private final int color;
    private final String displayName;

    VoRarity(int color, String displayName) {
        this.color = color;
        this.displayName = displayName;
    }

    public @NotNull Component applyColor(@NotNull Component c) {
        return c.color(TextColor.color(color));
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return String.format("#%06X", color) + " " + displayName;
    }
}
