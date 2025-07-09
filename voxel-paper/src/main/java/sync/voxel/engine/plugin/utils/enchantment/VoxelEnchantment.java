package sync.voxel.engine.plugin.utils.enchantment;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.api.enchantment.VoxEnchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoxelEnchantment implements VoxEnchantment {

    public static final Registry<@NotNull Enchantment> VANILLA_ENCHANTMENTS = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);

    private final VoxKey key;
    private final Map<String, Object> nbt = new HashMap<>();

    public static Set<VoxEnchantment> values() {
        return enchantments;
    }

    public static VoxEnchantment valueOf(String nameSpace, String identifier) {
        return enchantments.stream()
                .filter(e -> e.getKey().toString().equals(nameSpace + ":" + identifier))
                .findFirst()
                .orElse(null);
    }

    public static VoxEnchantment valueOf(String s) {
        return enchantments.stream()
                .filter(e -> e.getKey().toString().equals(s))
                .findFirst()
                .orElse(null);
    }

    public static VoxEnchantment valueOf(VoxKey key) {
        return enchantments.stream()
                .filter(e -> e.getKey().toString().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }

    public static VoxEnchantment valueOf(@NotNull Enchantment vaEnchantment) {
        return valueOf(vaEnchantment.getKey().toString());
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull VoxEnchantment forkEnchantment(VoxKey key) {
        return new VoxelEnchantment(key);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull VoxEnchantment editEnchantment(@NotNull Enchantment enchantment) {
        return VoxEnchantment.valueOf(enchantment.getKey().toString());
    }

    public static @NotNull String getRomanInteger(Integer number) {
        if (number == 0) return "N";

        boolean isNegative = number < 0;
        int absoluteValue = Math.abs(number);

        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        if (isNegative) roman.append("-");

        for (int i = 0; i < values.length; i++) {
            while (absoluteValue >= values[i]) {
                roman.append(numerals[i]);
                absoluteValue -= values[i];
            }
        }

        return roman.toString();
    }

    public VoxelEnchantment(VoxKey key) {
        this.key = key;
        enchantments.add(this);
    }

    @Override
    public VoxKey getKey() {
        return key;
    }

    @Override
    public <T> T getAttribute(String key, Class<T> type, T defaultValue) {
        return get("attribute", key, type, defaultValue);
    }

    /**
     * Gets a value from NBT storage using a namespaced key (format "namespace:key")
     */
    public <T> T get(String namespacedKey, @NotNull Class<T> type, T defaultValue) {
        Object value = nbt.get(namespacedKey);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return defaultValue;
    }

    /**
     * Stores a value in NBT storage using a namespaced key (format "namespace:key")
     * @return the previous value associated with the key, or null if there was none
     */
    public <T> T store(String namespacedKey, @NotNull Class<T> type, T value) {
        Object oldValue = nbt.put(namespacedKey, value);
        if (type.isInstance(oldValue)) {
            return type.cast(oldValue);
        }
        return null;
    }

    /**
     * Alternative get method that separates namespace and key
     */
    public <T> T get(String namespace, String key, Class<T> type, T defaultValue) {
        return get(namespace + ":" + key, type, defaultValue);
    }

    /**
     * Alternative store method that separates namespace and key
     */
    public <T> T store(String namespace, String key, Class<T> type, T value) {
        return store(namespace + ":" + key, type, value);
    }


}
