package sync.voxel.engine.paper.utils.voxel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sync.voxel.engine.api.common.VoxKey;
import sync.voxel.engine.paper.PaperPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TranslationManager {

    @Getter
    private static final Map<String, Map<String, String>> translationCache = new HashMap<>();
    private static final File langFolder =  new File("plugins/voxel/pack/overwrite/lang/");

    static  {
        if (!langFolder.exists()) {
            if(langFolder.mkdirs()) throw new RuntimeException("Can not create " + langFolder.getPath());
        }

    }

    public static String getNameFor(@NotNull Player player, @NotNull String type, @NotNull VoxKey key) {
        String locale = player.locale().toLanguageTag().replace("-", "_").toLowerCase();
        String transKey = type + '.' + key.toString('.');

        Map<String, String> langMap = getOrLoadTranslations(locale);
        if (langMap.containsKey(transKey)) {
            return langMap.get(transKey);
        }

        // fallback
        return key.identifier();
    }

    private static Map<String, String> getOrLoadTranslations(String locale) {
        if (translationCache.containsKey(locale)) {
            return translationCache.get(locale);
        }

        Map<String, String> translations = new HashMap<>();

        Map<String, String> onlineTranslations = downloadOnlineTranslation(locale);
        if (onlineTranslations != null) {
            translations.putAll(onlineTranslations);
        }

        Map<String, String> localOverrides = loadLocalOverrides(locale);
        translations.putAll(localOverrides); // überschreibt die online keys

        translationCache.put(locale, translations);
        return translations;
    }

    private static @NotNull Map<String, String> loadLocalOverrides(String locale) {
        Map<String, String> result = new HashMap<>();
        File file = new File(langFolder, locale + ".json");
        if (!file.exists()) return result;

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : json.entrySet()) {
                result.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (IOException e) {
            PaperPlugin.logger.warn("Could not load local translation file for " + locale + ": " + e.getMessage());
        }
        return result;
    }

    private static @Nullable Map<String, String> downloadOnlineTranslation(String locale) {
        try {
            String urlString = "https://voxelsync.github.io/translation/minecraft/" + locale + ".json";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            if (connection.getResponseCode() == 200) {
                try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    Map<String, String> result = new HashMap<>();
                    for (Map.Entry<String, com.google.gson.JsonElement> entry : json.entrySet()) {
                        result.put(entry.getKey(), entry.getValue().getAsString());
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            PaperPlugin.logger.warn("Failed to download online translation for " + locale + ": " + e.getMessage());
        }
        return null;
    }

}
