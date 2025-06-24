package sync.voxel.paper.utils.text;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class Label {
    private final Function<Language, Translation> label;

    private Label(String path, String key) {
        this.label = lang -> Text.translationOf(path, key, lang);
    }

    public String toString() {
        return label.apply(null).toString();
    }

    public Translation toTranslation() {
        return label.apply(null);
    }

    public Translation from(Language language) {
        return label.apply(language);
    }

    public Translation fromDefault() {
        return label.apply(null);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Label of(String path, String key) {
        return new Label(path, key);
    }

}
