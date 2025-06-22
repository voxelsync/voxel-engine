package sync.voxel.paper.runtime.enchantment;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sync.voxel.api.common.VoKey;
import sync.voxel.api.enchantment.VoEnchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoxelEnchantment implements VoEnchantment {

    private final VoKey key;
    private final Map<String, Object> nbt = new HashMap<>();

    public static Set<VoEnchantment> values() {
        return enchantments;
    }

    public static VoEnchantment valueOf(String nameSpace, String identifier) {
        return enchantments.stream()
                .filter(e -> e.getKey().toString().equals(nameSpace + ":" + identifier))
                .findFirst()
                .orElse(null);
    }

    public static VoEnchantment valueOf(String s) {
        return enchantments.stream()
                .filter(e -> e.getKey().toString().equals(s))
                .findFirst()
                .orElse(null);
    }

    public static VoEnchantment valueOf(VoKey key) {
        return enchantments.stream()
                .filter(e -> e.getKey().toString().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }

    public static VoEnchantment valueOf(@NotNull Enchantment vaEnchantment) {
        return valueOf(vaEnchantment.getKey().toString());
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull VoEnchantment forkEnchantment(VoKey key) {
        return new VoxelEnchantment(key);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull VoEnchantment editEnchantment(@NotNull Enchantment enchantment) {
        return VoEnchantment.valueOf(enchantment.getKey().toString());
    }

    public VoxelEnchantment(VoKey key) {
        this.key = key;
    }

    @Override
    public VoKey getKey() {
        return key;
    }

    @Override
    public int getTextureIdentifier() {
        return get("render:texture_identifier", Integer.class, 0);
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
