/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.plugin.utils.material;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.api.common.VoxRenderType;
import sync.voxel.engine.api.material.VoxMaterial;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VoxelMaterial implements VoxMaterial {

    private final Material vaMaterial;
    private final VoxKey key;
    private final VoxRenderType renderType;
    private final Map<String, Object> nbt = new HashMap<>();

    public static Set<VoxMaterial> values() {
        return materials;
    }

    public static VoxMaterial valueOf(String nameSpace, String identifier) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(nameSpace + ":" + identifier))
                .findFirst()
                .orElse(null);
    }

    public static VoxMaterial valueOf(String s) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(s))
                .findFirst()
                .orElse(null);
    }

    public static VoxMaterial valueOf(VoxKey key) {
        return materials.stream()
                .filter(m -> m.getKey().toString().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }

    public static VoxMaterial valueOf(@NotNull Material vaMaterial) {
        return valueOf(vaMaterial.getKey().toString());
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull VoxelMaterial forkMaterial(Material vaMaterial, VoxKey key, VoxRenderType renderType) {
        return new VoxelMaterial(vaMaterial, key, renderType);
    }


    public VoxelMaterial(Material vaMaterial, VoxKey key, VoxRenderType renderType) {
        this.renderType = renderType;
        this.vaMaterial = vaMaterial;
        this.key = key;
        materials.add(this);
    }

    @Override
    public VoxKey getKey() {
        return key;
    }

    @Override
    public String getNameFor(@NotNull Player player) {
        // Log initial parameters
        String playerName = player.getName();
        String locale = player.locale().toLanguageTag();
        System.out.println("Starting translation lookup for player: " + playerName + " with locale: " + locale);

        String fileName = locale.replace("-", "_").toLowerCase();
        String from = "item." + key.toString('.');
        System.out.println("Formatted filename: " + fileName + ", translation key: " + from);

        try {
            String urlString = "https://voxelsync.github.io/translation/minecraft/" + fileName + ".json";
            System.out.println("Attempting to connect to: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            System.out.println("Attempting to connect to: " + urlString + "Setting connection timeout to 2000ms");

            long startTime = System.currentTimeMillis();
            int responseCode = connection.getResponseCode();
            long duration = System.currentTimeMillis() - startTime;

            System.out.println("Attempting to connect to: " + urlString +"HTTP request completed. Response code: " + responseCode + ", took " + duration + "ms");

            if (responseCode == 200) {
                System.out.println("Attempting to connect to: " + urlString + "Successful response, reading data...");
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    System.out.println("Received JSON data, size: " + json.size() + " entries");

                    if (json.has(from)) {
                        String translation = json.get(from).getAsString();
                        System.out.println("Found translation for key '" + from + "': " + translation);
                        return translation;
                    } else {
                        System.out.println("Translation key '" + from + "' not found in JSON response");
                    }
                }
            } else {
                System.out.println("HTTP request failed with code: " + responseCode);
                try (InputStream errorStream = connection.getErrorStream()) {
                    if (errorStream != null) {
                        String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        System.out.println("Error response body: " + errorResponse);
                    }
                } catch (Exception e) {
                    System.out.println("Could not read error stream" + e);
                }
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL exception" + e);
        } catch (IOException e) {
            System.out.println("IO exception during translation lookup" + e);
        } catch (Exception e) {
            System.out.println("Unexpected exception during translation lookup" + e);
        }

        // Fallback
        System.out.println("Using fallback translation for key: " + key.identifier());
        return key.identifier();
    }

    @Override
    public Material getVaMaterial() {
        return vaMaterial;
    }

    @Override
    public VoxRenderType getRenderType() {
        return renderType;
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
