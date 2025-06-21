/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.api.common;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a namespaced key in the format "namespace:identifier".
 * <p>
 * This is similar to {@link NamespacedKey} but provides additional validation and utility methods.
 *
 * @see NamespacedKey
 */
public record VoKey(String namespace, String identifier) {

    /**
     * Creates a VoKey from a combined string in the format "namespace:identifier".
     *
     * @param combined the combined key string (e.g., "minecraft:stone")
     * @return a new VoKey instance
     * @throws IllegalArgumentException if the format is invalid or contains illegal characters
     */
    @Contract("_ -> new")
    public static @NotNull VoKey of(@NotNull String combined) {
        String[] parts = combined.split(":", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Key must be in format 'namespace:identifier'");
        }

        String namespace = parts[0];
        String identifier = parts[1];

        if (!isValidNamespace(namespace)) {
            throw new IllegalArgumentException("Invalid namespace: " + namespace);
        }

        if (!isValidKey(identifier)) {
            throw new IllegalArgumentException("Invalid identifier: " + identifier);
        }

        return new VoKey(namespace, identifier);
    }

    /**
     * Creates a VoKey from a Bukkit NamespacedKey.
     *
     * @param key the Bukkit NamespacedKey to convert
     * @return a new VoKey instance
     */
    @Contract("_ -> new")
    public static @NotNull VoKey of(@NotNull NamespacedKey key) {
        return of(key.toString());
    }

    @Override
    public @NotNull String toString() {
        return namespace + ":" + identifier;
    }

    /**
     * Validates a namespace string.
     *
     * @param namespace the namespace to validate
     * @return true if the namespace is valid
     */
    private static boolean isValidNamespace(@NotNull String namespace) {
        int len = namespace.length();
        if (len == 0) return false;

        for (int i = 0; i < len; i++) {
            if (isInValidChar(namespace.charAt(i))) return false;
        }

        return true;
    }

    /**
     * Validates a key identifier string.
     *
     * @param key the key to validate
     * @return true if the key is valid
     */
    private static boolean isValidKey(@NotNull String key) {
        int len = key.length();
        if (len == 0) return false;

        for (int i = 0; i < len; i++) {
            if (isInValidChar(key.charAt(i))) return false;
        }

        return true;
    }

    /**
     * Checks if a character is valid.
     *
     * @param c the character to check
     * @return true if the character is valid
     */
    private static boolean isInValidChar(char c) {
        return (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '.' && c != '_' && c != '-';
    }

    /**
     * Compares this VoKey to the specified object for equality.
     * <p>
     * Two VoKeys are considered equal if both their namespace and identifier are equal.
     *
     * @param o the object to compare with
     * @return true if the given object represents a VoKey equivalent to this one, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoKey voKey = (VoKey) o;
        return namespace.equals(voKey.namespace) && identifier.equals(voKey.identifier);
    }

    /**
     * Returns a hash code value for this VoKey.
     * <p>
     * The hash code is computed based on both the namespace and identifier.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return 31 * namespace.hashCode() + ":".hashCode() + identifier.hashCode();
    }
}