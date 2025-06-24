package sync.voxel.paper.utils.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a translated text with placeholder and legacy color support.
 * <p>
 * Placeholders must be in the format {@code %placeholder%} and will be replaced
 * by values from the provided map. Color formatting is supported via {@code &}-codes
 * (e.g. {@code &a}, {@code &l}) and hex color codes (e.g. {@code &#ff0000}).
 * <p>
 * Example value:
 * {@code "&aHello, %name%!"}
 * <br>
 * Replacement: {@code %name% -> "&bLenny"} → Result: "Hello, Lenny" with colors.
 *
 * @param key   The translation key used to identify this text
 * @param value The actual translated text with placeholders and legacy formatting
 */
public record Translation(
        String key,
        String value) {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([a-zA-Z0-9_]+)%");

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();

    /**
     * Returns the raw translation value without any placeholder processing.
     *
     * @return the unformatted text string
     */
    @Override
    public @NotNull String toString() {
        return value;
    }

    /**
     * Converts the raw value to a Component using legacy formatting codes.
     *
     * @return the raw Component with colors
     */
    @Contract(value = " -> new", pure = true)
    public @NotNull Component toRawComponent() {
        return deserializeLegacy(value);
    }

    /**
     * Alias for {@link #toRawComponent()} – returns the legacy-colored component without replacements.
     *
     * @return the legacy-formatted Component
     */
    @Contract(value = " -> new", pure = true)
    public @NotNull Component toComponent() {
        return deserializeLegacy(value);
    }

    /**
     * Replaces a single placeholder in this translation.
     * <p>
     * The replacement may also contain color codes.
     *
     * @param placeholder the placeholder name (without %)
     * @param value       the replacement value
     * @return the formatted Component
     */
    public @NotNull Component format(String placeholder, String value) {
        return format(Map.of(placeholder, value));
    }

    /**
     * Replaces placeholders in the format {@code %placeholder%} with values from the provided map.
     * <p>
     * The result is parsed as legacy-formatted Component, including color codes in the base text
     * and replacements.
     * <p>
     * Unknown placeholders remain untouched (e.g. {@code %unknown%}).
     *
     * @param replacements map of placeholder names (without %) to their replacement values
     * @return the fully formatted Component
     */
    public @NotNull Component format(Map<String, String> replacements) {
        if (replacements == null || replacements.isEmpty()) {
            return deserializeLegacy(value);
        }

        StringBuilder resultBuilder = new StringBuilder();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        int lastEnd = 0;

        while (matcher.find()) {
            resultBuilder.append(value, lastEnd, matcher.start());

            String placeholder = matcher.group(1);
            String replacement = replacements.getOrDefault(placeholder, matcher.group());

            resultBuilder.append(replacement);
            lastEnd = matcher.end();
        }

        if (lastEnd < value.length()) {
            resultBuilder.append(value.substring(lastEnd));
        }

        return deserializeLegacy(resultBuilder.toString());
    }

    /**
     * Internal utility for deserializing {@code &}-formatted text with optional {@code &#rrggbb} hex codes.
     *
     * @param input the legacy string
     * @return the parsed Component
     */
    private static @NotNull Component deserializeLegacy(String input) {
        return LEGACY.deserialize(input);
    }
}
