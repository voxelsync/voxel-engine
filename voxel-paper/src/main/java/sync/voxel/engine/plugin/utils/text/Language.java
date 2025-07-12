/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.plugin.utils.text;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a language with its metadata and file path.
 * Manages caching of language instances for efficient retrieval.
 */
public record Language(
        String id,
        String path,
        String name
) {
    private static final String DEFAULT_LANG_PATH = "./lang";
    private static final Map<String, Language> langCache = new HashMap<>();

    /**
     * Retrieves a language instance from cache or creates a new one.
     * @param filePath Path to the language file (relative or absolute)
     * @return Language instance or null if file doesn't exist
     */
    public static @Nullable Language of(String filePath) {
        if (langCache.containsKey(filePath)) {
            return langCache.get(filePath);
        }

        String fullPath;
        if (!filePath.contains("/") && !filePath.contains("\\")) {
            fullPath = Text.getLangPath() + File.separator + filePath;
        } else {
            fullPath = filePath;
        }

        File langFile = new File(fullPath);
        if (!langFile.exists() || !langFile.isFile()) {
            return null;
        }

        String langId = langFile.getName().replace(".json", "");
        String langName = langId.split("-")[0];

        Language language = new Language(langId, fullPath, langName);
        langCache.put(filePath, language);

        return language;
    }

    /**
     * Clears all cached language instances.
     */
    public static void clearCache() {
        langCache.clear();
    }
}

