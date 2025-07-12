package sync.voxel.engine.plugin.common.logger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VoxLogger {
    // ===== LOGGER FACTORY AND REGISTRY =====

    private static CommandSender console = Bukkit.getConsoleSender();
    private static HashMap<String, VoxLogger> loggers = new HashMap();

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull VoxLogger registerLogger(String id) {
        return new VoxLogger(id);
    }

    // ===== MAIN LOGGER PART =====

    private final String id;
    private Component prefix;
    private List<Component> icon;

    private VoxLogger(String id) {
        this.id = id;
        loggers.put(id, this);
    }

    public VoxLogger setPrefix(Component prefix) {
        this.prefix = prefix;
        return this;
    }

    public void setIcon(char letter, int color) {
        setIcon(upscale(letter), color);
    }

    public void setIcon(@NotNull List<String> letterIcon, int color) {
        List<Component> icon = new ArrayList<>();
        List<String> preIcon = upscale('V');

        int size = Math.min(preIcon.size(), letterIcon.size());
        for (int i = 0; i < size; i++) {
            Component line = Component.empty().append(Component.text(preIcon.get(i), TextColor.color(0xFF685B)),
                    Component.text(" "), Component.text(letterIcon.get(i), TextColor.color(color)));
            icon.add(line);
        }

        this.icon = icon;
    }

    public void warn(String message) {
        send(Component.text(message), TextColor.color(0xB59300));
    }

    public void debug(String message) {
        if (new File("./i.m.a.dev.trust.me").exists()) {
            send(Component.text(message), TextColor.color(0x007FB2));
        }
    }

    public void info(String message) {
        send(Component.text(message), TextColor.color(0xFF685B));
    }

    public void send(@NotNull Component message, TextColor color) {
        Component prefix = Component.empty().append(
                Component.text("["),
                Component.text("V", color),
                Component.text("]["),
                this.prefix,
                Component.text("] "));

        console.sendMessage(prefix.append(message));
    }

    public void announce(@NotNull Component... lines) {
        int length = PlainTextComponentSerializer.plainText().serialize(icon.get(0)).length();
        String iconSpace = " ".repeat(length);
        Component space = Component.text(" ".repeat(12 - length), TextColor.color(0xFFFFFF));

        console.sendMessage(icon.get(0));
        console.sendMessage(icon.get(1).append(space, getOrEmpty(lines, 0)));
        console.sendMessage(icon.get(2).append(space, getOrEmpty(lines, 1)));
        console.sendMessage(Component.text(iconSpace).append(space, getOrEmpty(lines, 2)));

        for (int i = 3; i < lines.length; i++) {
            console.sendMessage(Component.text(iconSpace).append(space, lines[i]));
        }
    }


    private Component getOrEmpty(Component[] lines, int index) {
        if (index >= 0 && index < lines.length) {
            return lines[index];
        }
        return Component.empty();
    }

    private @NotNull @Unmodifiable List<String> upscale(char letter) {
        return switch (Character.toUpperCase(letter)) {
            case 'A' -> List.of(
                    "    ",
                    " /\\ ",
                    "/--\\"
            );
            case 'B' -> List.of(
                    " __ ",
                    "|__]",
                    "|__]"
            );
            case 'C' -> List.of(
                    " __",
                    "/  ",
                    "\\__"
            );
            case 'D' -> List.of(
                    " _ ",
                    "| \\",
                    "|_/"
            );
            case 'E' -> List.of(
                    " __",
                    "|__",
                    "|__"
            );
            case 'F' -> List.of(
                    " ___",
                    "|__ ",
                    "|   "
            );
            case 'G' -> List.of(
                    " __ ",
                    "| _ ",
                    "|__|"
            );
            case 'H' -> List.of(
                    "    ",
                    "|__|",
                    "|  |"
            );
            case 'I' -> List.of(
                    " ",
                    "|",
                    "|"
            );
            case 'J' -> List.of(
                    "   ",
                    "  |",
                    "\\_|"
            );
            case 'K' -> List.of(
                    "   ",
                    "|/ ",
                    "|\\ "
            );
            case 'L' -> List.of(
                    "   ",
                    "|  ",
                    "|__"
            );
            case 'M' -> List.of(
                    "    ",
                    "|\\/|",
                    "|  |"
            );
            case 'N' -> List.of(
                    "    ",
                    "|\\ |",
                    "| \\|"
            );
            case 'O' -> List.of(
                    " _ ",
                    "/ \\",
                    "\\_/"
            );
            case 'P' -> List.of(
                    " __ ",
                    "|__|",
                    "|   "
            );
            case 'Q' -> List.of(
                    " _ ",
                    "/ \\",
                    "\\_X"
            );
            case 'R' -> List.of(
                    " __",
                    "|_/",
                    "| \\"
            );
            case 'S' -> List.of(
                    " ___",
                    "/__ ",
                    "__/ "
            );
            case 'T' -> List.of(
                    "___",
                    " | ",
                    " | "
            );
            case 'U' -> List.of(
                    "    ",
                    "|  |",
                    "|__|"
            );
            case 'V' -> List.of(
                    "    ",
                    "\\  /",
                    " \\/ "
            );
            case 'W' -> List.of(
                    "    ",
                    "|  |",
                    "|/\\|"
            );
            case 'X' -> List.of(
                    "  ",
                    "\\/",
                    "/\\"
            );
            case 'Y' -> List.of(
                    "   ",
                    "\\ /",
                    " | "
            );
            case 'Z' -> List.of(
                    "__",
                    " /",
                    "/_"
            );
            default -> List.of(
                    "???",
                    "???",
                    "???"
            );
        };
    }

}
