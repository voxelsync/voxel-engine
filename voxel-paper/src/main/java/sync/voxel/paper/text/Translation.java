package sync.voxel.paper.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a translated text with placeholder support.
 * <p>
 * Placeholders must be in the format {@code %placeholder%} and will be replaced
 * by values from the format mapold. Other formats like {@code <placeholder>} will
 * not be processed.
 *
 * @param key The text key used to identify this text
 * @param value The actual translated text with potential placeholders
 */
public record Translation(
        String key,
        String value) {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([a-zA-Z0-9_]+)%");
    private static final TextColor DEFAULT_TEXT_COLOR = TextColor.color(0xAAAAAA); // Gray
    private static final int DEFAULT_REPLACEMENT_COLOR = 0x55FFFF; // Cyan
    private static final int ERROR_REPLACEMENT_COLOR = 0xED727C; // Red soft

    /**
     * Returns the raw translated value without any placeholder processing.
     * @return The unmodified text value
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Alias for {@link #toComponent()} - returns the raw translated value.
     * @return The unmodified text value
     */
    @Contract(value = " -> new", pure = true)
    public @NotNull Component toRawComponent() {
        return Component.text(value).color(DEFAULT_TEXT_COLOR);
    }

    /**
     * Alias for {@link #toString()} - returns the raw translated value.
     * @return The unmodified text value as Component
     */
    @Contract(value = " -> new", pure = true)
    public @NotNull Component toComponent() {
        return Component.text(value).color(DEFAULT_TEXT_COLOR);
    }

    /**
     * Alias for {@link #format(Map, int)} - replacements and colored.
     * @return The formatted Component with colored replacements
     */
    public Component format(String placeholder, String value) {
        return format(placeholder, value, DEFAULT_REPLACEMENT_COLOR);
    }

    /**
     * Alias for {@link #format(Map, int)} - replacements and colored.
     * @return The formatted Component with colored replacements
     */
    public Component formatToError(String placeholder, String value) {
        return format(placeholder, value, ERROR_REPLACEMENT_COLOR);
    }

    /**
     * Alias for {@link #format(Map, int)} - replacements and colored.
     * @return The formatted Component with colored replacements
     */
    public Component format(String placeholder, String value, int replacementColor) {
        return format(Map.of(placeholder, value), replacementColor);
    }

    /**
     * Replaces placeholders in the format {@code %placeholder%} with values from the provided mapold,
     * returning a Component where the base text is gray and replacements are colored.
     * <p>
     * Example:
     * <pre>{@code
     * Translation t = new Translation("key", "Use %command%");
     * Component formatted = t.format(Map.of("command", "/msg <player>"), TextColor.color(0xFFAA00));
     * // Result: "Use /msg <player>" where "Use " is gray and "/msg <player>" is orange
     * }</pre>
     *
     * @param replacements Map of placeholder names (without % symbols) to their replacement values
     * @param replacementColor The color for the replacement text
     * @return The formatted Component with colored replacements
     */
    public Component format(Map<String, String> replacements, int replacementColor) {
        if (replacements == null || replacements.isEmpty()) {
            return Component.text(value).color(DEFAULT_TEXT_COLOR);
        }

        Component result = Component.empty();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        int lastEnd = 0;

        while (matcher.find()) {
            if (lastEnd < matcher.start()) {
                result = result.append(
                        Component.text(value.substring(lastEnd, matcher.start()))
                                .color(DEFAULT_TEXT_COLOR)
                );
            }

            String placeholder = matcher.group(1);
            String replacement = replacements.getOrDefault(placeholder, matcher.group());

            // Add replacement in special color
            if (replacements.containsKey(placeholder)) {
                result = result.append(
                        Component.text(replacement)
                                .color(TextColor.color(replacementColor))
                );
            } else {
                result = result.append(
                        Component.text(replacement)
                                .color(DEFAULT_TEXT_COLOR)
                );
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < value.length()) {
            result = result.append(
                    Component.text(value.substring(lastEnd))
                            .color(DEFAULT_TEXT_COLOR)
            );
        }

        return result;
    }

    /**
     * Overload of format with default replacement color.
     */
    public Component format(Map<String, String> replacements) {
        return format(replacements, DEFAULT_REPLACEMENT_COLOR);
    }
}