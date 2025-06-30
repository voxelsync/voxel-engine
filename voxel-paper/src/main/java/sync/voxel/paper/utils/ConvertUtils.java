package sync.voxel.paper.utils;

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
