/**
 * VOXEL-LICENSE NOTICE
 * <br><br>
 * This software is part of VoxelSync under the Voxel Public License. <br>
 * Source at: <a href="https://github.com/voxelsync/voxel/blob/main/LICENSE">GITHUB</a>
 * <br><br>
 * Copyright (c) Ley <cm.ley.cm@gmail.com> <br>
 * Copyright (c) contributors
 */
package sync.voxel.engine.paper.utils.logger;

import lombok.Getter;
import lombok.Setter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import sync.voxel.engine.paper.PaperPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class VoxLogger {
    private static final CommandSender console = Bukkit.getConsoleSender();
    private static final HashMap<String, VoxLogger> loggers = new HashMap<>();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ConcurrentLinkedQueue<Consumer<VoxLogger>> taskQueue = new ConcurrentLinkedQueue<>();
    private static boolean taskScheduled = false;

    // Global settings
    private static boolean enableFileLogging = true;
    private static final String logDirectory = "./logs/";


    @Contract(value = "_ -> new", pure = true)
    public static @NotNull VoxLogger of(String id) {
        return loggers.getOrDefault(id, new VoxLogger(id));
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull VoxLogger getOrCreate(String id) {
        return loggers.getOrDefault(id, new VoxLogger(id));
    }

    public static void setGlobalFileLogging(boolean enabled) {
        enableFileLogging = enabled;
    }

    // ===== TASK SCHEDULING =====
    private static void scheduleTask(Consumer<VoxLogger> task) {
        taskQueue.offer(task);
        if (!taskScheduled) {
            taskScheduled = true;
            Bukkit.getScheduler().runTask(PaperPlugin.plugin, () -> {
                Consumer<VoxLogger> nextTask;
                while ((nextTask = taskQueue.poll()) != null) {
                    try {
                        for (VoxLogger logger : loggers.values()) {
                            nextTask.accept(logger);
                        }
                    } catch (Exception e) {
                        console.sendMessage(Component.text("Logger task failed: " + e.getMessage(), TextColor.color(0xFF0000)));
                    }
                }
                taskScheduled = false;
            });
        }
    }

    // ===== MAIN LOGGER PART =====
    @Getter
    private final String id;
    private Component prefix;
    private List<Component> icon;
    @Setter
    private boolean fileLogging = true;
    @Setter
    private LogLevel minLevel = LogLevel.DEBUG;

    public enum LogLevel {
        DEBUG(0), INFO(1), WARN(2), ERROR(3), FATAL(4);

        private final int priority;

        LogLevel(int priority) {
            this.priority = priority;
        }

        @Contract(pure = true)
        public boolean isAtLeast(@NotNull LogLevel other) {
            return this.priority >= other.priority;
        }
    }

    private VoxLogger(String id) {
        this.id = id;
        loggers.put(id, this);
        ensureLogDirectory();
    }

    // ===== CONFIGURATION METHODS =====

    public void setPrefix(Component prefix) {
        this.prefix = prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = Component.text(prefix);
    }

    public void setIcon(char letter, int color) {
        setIcon(upscale(letter), color);
    }

    public void setIcon(@NotNull List<String> letterIcon, int color) {
        List<Component> icon = new ArrayList<>();
        List<String> preIcon = upscale('V');

        int size = Math.min(preIcon.size(), letterIcon.size());
        for (int i = 0; i < size; i++) {
            Component line = Component.empty().append(
                    Component.text(preIcon.get(i), TextColor.color(0xFF685B)),
                    Component.text(" "),
                    Component.text(letterIcon.get(i), TextColor.color(color))
            );
            icon.add(line);
        }

        this.icon = icon;
    }

    // ===== LOGGING METHODS =====

    public void fatal(String message, Object... args) {
        scheduleTask(logger -> fatalInternal(formatMessage(message, args)));
    }

    public void fatal(@NotNull Component message) {
        scheduleTask(logger -> fatalInternal(message));
    }

    private void fatalInternal(String message) {
        fatalInternal(Component.text(message));
    }

    private void fatalInternal(@NotNull Component message) {
        log(LogLevel.FATAL, message.color(TextColor.color(0xFF0000)), TextColor.color(0xFF0000));
    }

    public void error(String message, Object... args) {
        scheduleTask(logger -> errorInternal(formatMessage(message, args)));
    }

    public void error(@NotNull Component message) {
        scheduleTask(logger -> errorInternal(message));
    }

    private void errorInternal(String message) {
        errorInternal(Component.text(message));
    }

    private void errorInternal(@NotNull Component message) {
        log(LogLevel.ERROR, message.color(TextColor.color(0xB50100)), TextColor.color(0xB50100));
    }

    public void warn(String message, Object... args) {
        scheduleTask(logger -> warnInternal(formatMessage(message, args)));
    }

    public void warn(@NotNull Component message) {
        scheduleTask(logger -> warnInternal(message));
    }

    private void warnInternal(String message) {
        warnInternal(Component.text(message));
    }

    private void warnInternal(@NotNull Component message) {
        log(LogLevel.WARN, message.color(TextColor.color(0xB59300)), TextColor.color(0xB59300));
    }

    public void softWarn(String message, Object... args) {
        scheduleTask(logger -> softWarnInternal(formatMessage(message, args)));
    }

    public void softWarn(@NotNull Component message) {
        scheduleTask(logger -> softWarnInternal(message));
    }

    private void softWarnInternal(String message) {
        softWarnInternal(Component.text(message));
    }

    private void softWarnInternal(@NotNull Component message) {
        log(LogLevel.WARN, message.color(TextColor.color(0xFBFF9A)), TextColor.color(0xFBFF9A));
    }

    public void info(String message, Object... args) {
        scheduleTask(logger -> infoInternal(formatMessage(message, args)));
    }

    public void info(@NotNull Component message) {
        scheduleTask(logger -> infoInternal(message));
    }

    private void infoInternal(String message) {
        infoInternal(Component.text(message));
    }

    private void infoInternal(@NotNull Component message) {
        log(LogLevel.INFO, message.color(TextColor.color(0x666666)), TextColor.color(0x666666));
    }

    public void debug(String message, Object... args) {
        scheduleTask(logger -> debugInternal(formatMessage(message, args)));
    }

    public void debug(Component message) {
        scheduleTask(logger -> debugInternal(message));
    }

    private void debugInternal(String message) {
        debugInternal(Component.text(message));
    }

    private void debugInternal(Component message) {
        File file = new File("this-is.dev");
        if (file.exists()) {
            log(LogLevel.DEBUG, message.color(TextColor.color(0x007FB2)), TextColor.color(0x007FB2));
        }
    }

    public void trace(String message, Object... args) {
        scheduleTask(logger -> traceInternal(formatMessage(message, args)));
    }

    public void trace(Component message) {
        scheduleTask(logger -> traceInternal(message));
    }

    private void traceInternal(String message) {
        traceInternal(Component.text(message));
    }

    private void traceInternal(Component message) {
        File file = new File("this-is.dev");
        if (file.exists()) {
            log(LogLevel.DEBUG, message.color(TextColor.color(0x888888)), TextColor.color(0x888888));
        }
    }

    public void success(String message, Object... args) {
        scheduleTask(logger -> successInternal(formatMessage(message, args)));
    }

    public void success(@NotNull Component message) {
        scheduleTask(logger -> successInternal(message));
    }

    private void successInternal(String message) {
        successInternal(Component.text(message));
    }

    private void successInternal(@NotNull Component message) {
        log(LogLevel.INFO, message.color(TextColor.color(0x00FF00)), TextColor.color(0x00FF00));
    }

    public void highlight(String message, Object... args) {
        scheduleTask(logger -> highlightInternal(formatMessage(message, args)));
    }

    public void highlight(@NotNull Component message) {
        scheduleTask(logger -> highlightInternal(message));
    }

    private void highlightInternal(String message) {
        highlightInternal(Component.text(message));
    }

    private void highlightInternal(@NotNull Component message) {
        log(LogLevel.INFO, message.color(TextColor.color(0xFFFF00)).decorate(TextDecoration.BOLD), TextColor.color(0xFFFF00));
    }

    public void critical(String message, Object... args) {
        scheduleTask(logger -> criticalInternal(formatMessage(message, args)));
    }

    public void critical(@NotNull Component message) {
        scheduleTask(logger -> criticalInternal(message));
    }

    private void criticalInternal(String message) {
        criticalInternal(Component.text(message));
    }

    private void criticalInternal(@NotNull Component message) {
        Component criticalMessage = message
                .color(TextColor.color(0xFF0000))
                .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED);
        log(LogLevel.ERROR, criticalMessage, TextColor.color(0xFF0000));
    }

    // ===== ADVANCED LOGGING METHODS =====

    public void logWithLevel(LogLevel level, String message, Object... args) {
        scheduleTask(logger -> logWithLevelInternal(level, formatMessage(message, args)));
    }

    public void logWithLevel(@NotNull LogLevel level, Component message) {
        scheduleTask(logger -> logWithLevelInternal(level, message));
    }

    private void logWithLevelInternal(LogLevel level, String message) {
        logWithLevelInternal(level, Component.text(message));
    }

    private void logWithLevelInternal(@NotNull LogLevel level, Component message) {
        TextColor color = switch (level) {
            case DEBUG -> TextColor.color(0x007FB2);
            case INFO -> TextColor.color(0x666666);
            case WARN -> TextColor.color(0xB59300);
            case ERROR -> TextColor.color(0xB50100);
            case FATAL -> TextColor.color(0xFF0000);
        };
        log(level, message, color);
    }

    public void separator() {
        separator("-");
    }

    public void separator(@NotNull String character) {
        String line = character.repeat(50);
        info(line);
    }

    public void header(String title) {
        separator("=");
        info(title);
        separator("=");
    }

    public void subHeader(String title) {
        separator("-");
        info(title);
        separator("-");
    }

    // ===== CORE LOGGING METHOD =====

    private void log(@NotNull LogLevel level, Component message, TextColor color) {
        if (!level.isAtLeast(minLevel)) {
            return;
        }

        Component finalMessage = buildMessage(level, message);
        console.sendMessage(finalMessage);

        if (fileLogging && enableFileLogging) {
            writeToFile(level, message);
        }
    }

    private @NotNull Component buildMessage(LogLevel level, Component message) {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

        // Logger prefix
        Component prefix = Component.empty().append(
                Component.text("["),
                Component.text("Voxel", TextColor.color(0xB50100)),
                this.prefix != null ? this.prefix : Component.text(id),
                Component.text("] ")
        );
        builder.append(prefix);

        // Message
        builder.append(message);

        return builder.build();
    }

    // ===== ANNOUNCEMENT METHODS =====

    public void announce(@NotNull Component... lines) {
        if (icon == null) {
            // Fallback if no icon is set
            for (Component line : lines) {
                info(line);
            }
            return;
        }

        int length = PlainTextComponentSerializer.plainText().serialize(icon.getFirst()).length();
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

    public void announce(String @NotNull ... lines) {
        Component[] components = new Component[lines.length];
        for (int i = 0; i < lines.length; i++) {
            components[i] = Component.text(lines[i]);
        }
        announce(components);
    }

    public void bigAnnounce(String title, String @NotNull ... lines) {
        separator("=");
        announce(title);
        if (lines.length > 0) {
            separator("-");
            for (String line : lines) {
                info(line);
            }
        }
        separator("=");
    }

    // ===== UTILITY METHODS =====

    private String formatMessage(String message, Object @NotNull ... args) {
        if (args.length == 0) {
            return message;
        }

        try {
            return String.format(message, args);
        } catch (Exception e) {
            return message + " [FORMATTING ERROR: " + e.getMessage() + "]";
        }
    }

    private Component getOrEmpty(Component[] lines, int index) {
        if (index >= 0 && index < lines.length) {
            return lines[index];
        }
        return Component.empty();
    }

    private void ensureLogDirectory() {
        File dir = new File(logDirectory);
        if (!dir.exists()) {
            boolean feedback = dir.mkdirs();
        }
    }

    private void writeToFile(@NotNull LogLevel level, Component message) {
        try {
            String fileName = logDirectory + id + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";

            File logFile = new File(fileName);

            try (FileWriter writer = new FileWriter(logFile, true)) {
                String timestamp = LocalDateTime.now().format(DATE_FORMAT);
                String plainMessage = PlainTextComponentSerializer.plainText().serialize(message);
                writer.write(String.format("[%s] [%s] %s%n", timestamp, level.name(), plainMessage));
            }
        } catch (IOException e) {
            console.sendMessage(Component.text("Failed to write to log file: " + e.getMessage(), TextColor.color(0xFF0000)));
        }
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