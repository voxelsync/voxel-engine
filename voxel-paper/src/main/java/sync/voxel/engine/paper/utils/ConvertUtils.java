/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.paper.utils;

import org.jetbrains.annotations.Contract;

import java.util.Optional;

public class ConvertUtils {

    @Contract(pure = true)
    public static Optional<Integer> getInt(Object input) {
        if (input instanceof String s) {
            try {
                return Optional.of(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }

        }
        return Optional.empty();
    }

}
