/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.paper.startup.reader;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.leycm.storage.Storage;
import org.leycm.storage.impl.JavaStorage;
import sync.voxel.engine.paper.PaperPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegistryReader {

    @Getter
    private static Set<Storage> storages = new HashSet<>();

    private static void read() {
        List<String> paths = RegistryReader.findfiles("plugins/voxel/pack/registry", ".json");

        paths.forEach(path -> {
            Storage storage = Storage.of("pack/registry/" + path, Storage.Type.JSON, JavaStorage.class);
            storages.add(storage);
        });



    }

    private static @NotNull List<String> findfiles(String rootFolderPath, String fileExtension) {
        List<String> filePaths = new ArrayList<>();
        File rootFolder = new File(rootFolderPath);

        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            PaperPlugin.logger.warn("Der angegebene Pfad existiert nicht oder ist kein Ordner: " + rootFolderPath);
            return filePaths;
        }

        searchFiles(rootFolder, fileExtension, filePaths, rootFolder.getPath());
        return filePaths;
    }

    private static void searchFiles(@NotNull File folder, String fileExtension,
                                    List<String> fileList, String rootPath) {
        File[] files = folder.listFiles();

        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                searchFiles(file, fileExtension, fileList, rootPath);
            } else if (file.getName().toLowerCase().endsWith(fileExtension.toLowerCase())) {
                String relativePath = getRelativePath(file.getPath(), rootPath);
                fileList.add(relativePath);
            }
        }
    }

    private static @NotNull String getRelativePath(@NotNull String absolutePath, @NotNull String rootPath) {
        String relativePath = absolutePath.substring(rootPath.length());
        if (relativePath.startsWith(File.separator)) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    public static void main(String[] args) {
        String startFolder = ".";
        String extension = ".json";

        List<String> files = findfiles(startFolder, extension);

        PaperPlugin.logger.debug("Gefundene JSON-Dateien:");
        for (String filePath : files) {
            PaperPlugin.logger.debug(filePath);
        }
    }
}
