/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.paper.utils.text;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * A handler class for managing and retrieving translations from language files.
 * <p>
 * This class provides functionality to load language files in JSON format,
 * cache them for performance, and retrieve translations for specific keys.
 * It supports fallback to a default language (en-en.json) if the requested language is not available.
 */
public final class Text {
    private static final String DEFAULT_LANG_DIR = ".lang";
    private static final Map<String, JsonObject> langCache = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    private static String langPath;

    private Text() {}

    /**
     * Initializes the text handler with the specified language directory path.
     * If the provided path is null, the default directory (.lang) will be used.
     *
     * @param path The path to the directory containing language files
     */
    public static void registerLangDirectory(String path) {
        Text.langPath = path != null ? path : DEFAULT_LANG_DIR;
    }

    /**
     * Retrieves a text for the given key using the default language (en-en).
     * The text is searched in the "messages" path of the language file.
     *
     * @param key The key of the text to retrieve
     * @return A Translation object containing the key and its translated value
     */
    public static @NotNull @Unmodifiable Label labelOf(String key) {
        return Label.of("messages", key);
    }

    /**
     * Retrieves a text for the given key using the default language (en-en).
     * The text is searched in the specified path of the language file.
     *
     * @param path The path within the JSON structure where the text is located
     * @param key The key of the text to retrieve
     * @return A Translation object containing the key and its translated value
     */
    public static @NotNull @Unmodifiable Label labelOf(String path, String key) {
        return Label.of(path, key);
    }

    /**
     * Retrieves a text for the given key using the default language (en-en).
     * The text is searched in the "messages" path of the language file.
     *
     * @param key The key of the text to retrieve
     * @return A Translation object containing the key and its translated value
     */
    public static @NotNull Translation translationOf(String key) {
        return getTranslationFromJson("messages", key, Language.of("en-en.json"));
    }

    /**
     * Retrieves a text for the given key using the specified language.
     * The text is searched in the "messages" path of the language file.
     *
     * @param key The key of the text to retrieve
     * @param lang The language to use for the text
     * @return A Translation object containing the key and its translated value
     */
    public static @NotNull Translation translationOf(String key, Language lang) {
        return getTranslationFromJson("messages", key, lang);
    }

    /**
     * Retrieves a text for the given key using the default language (en-en).
     * The text is searched in the specified path of the language file.
     *
     * @param path The path within the JSON structure where the text is located
     * @param key The key of the text to retrieve
     * @return A Translation object containing the key and its translated value
     */
    public static @NotNull Translation translationOf(String path, String key) {
        return getTranslationFromJson(path, key, Language.of("en-en.json"));
    }

    /**
     * Retrieves a text for the given key using the specified language.
     * The text is searched in the specified path of the language file.
     *
     * @param path The path within the JSON structure where the text is located
     * @param key The key of the text to retrieve
     * @param lang The language to use for the text (null will return the key as the text)
     * @return A Translation object containing the key and its translated value
     */
    @Contract("_, _, null -> new")
    public static @NotNull Translation translationOf(String path, String key, Language lang) {
        return getTranslationFromJson(path, key, lang);
    }

    /**
     * Internal method to retrieve a text from a JSON language file.
     *
     * @param path The path within the JSON structure where the text is located
     * @param key The key of the text to retrieve
     * @param lang The language to use for the text
     * @return A Translation object containing the key and its translated value
     *         (returns the key as the text if the path/key is not found)
     */
    private static @NotNull Translation getTranslationFromJson(String path, String key, Language lang) {
        if (lang == null) return new Translation(path + "/" + key, key);

        try {
            JsonObject rootObject = loadLanguageFile(lang.id());
            if (rootObject == null) return new Translation(path + "/" + key, key);

            JsonElement currentElement = rootObject;
            if (path != null && !path.isEmpty()) {
                for (String part : path.split("\\.")) {
                    if (!currentElement.isJsonObject()) return new Translation(path + "/" + key, key);
                    currentElement = currentElement.getAsJsonObject().get(part);
                    if (currentElement == null || currentElement.isJsonNull()) return new Translation(path + "/" + key, key);
                }
            }

            if (!currentElement.isJsonObject()) return new Translation(path + "/" + key, key);
            JsonElement valueElement = currentElement.getAsJsonObject().get(key);
            if (valueElement == null || valueElement.isJsonNull()) return new Translation(path + "/" + key, key);

            if (valueElement.isJsonPrimitive() && valueElement.getAsJsonPrimitive().isString()) {
                return new Translation(path + "/" + key, valueElement.getAsString());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new Translation(path + "/" + key, key);
    }

    /**
     * Loads a language file from disk or returns a cached version if available.
     * If the requested language file doesn't exist, falls back to en-en.json.
     *
     * @param langCode The language code of the file to load (e.g., "en-en")
     * @return The parsed JsonObject containing all translations for the language
     * @throws IOException If there's an error reading the language file
     */
    public static @Nullable JsonObject loadLanguageFile(@NotNull String langCode) throws IOException {
        if (langCache.containsKey(langCode)) return langCache.get(langCode);

        String fullPath = Paths.get(langPath, langCode + ".json").toString();
        File langFile = new File(fullPath);

        if (!langFile.exists()) {
            String fallbackPath = Paths.get(langPath, "en-en.json").toString();
            File fallbackFile = new File(fallbackPath);
            if (!fallbackFile.exists()) return null;

            String content = new String(Files.readAllBytes(Paths.get(fallbackPath)));
            JsonObject rootObject = gson.fromJson(content, JsonObject.class);
            langCache.put(langCode, rootObject);
            return rootObject;
        }

        String content = new String(Files.readAllBytes(Paths.get(fullPath)));
        JsonObject rootObject = gson.fromJson(content, JsonObject.class);
        langCache.put(langCode, rootObject);
        return rootObject;
    }

    /**
     * Clears the cache of loaded language files.
     * This forces the handler to reload language files from disk on next access.
     */
    public static void clearCache() {
        langCache.clear();
    }
}