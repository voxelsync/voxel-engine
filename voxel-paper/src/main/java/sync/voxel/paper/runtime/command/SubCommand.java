package sync.voxel.paper.runtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class SubCommand {

    private String key;

    public SubCommand(String... keys) {
        Arrays.stream(keys).toList().forEach(key -> {
            this.key = key;
            MainCommand.registerSubCommand(this);
        });
    }

    public SubCommand(String key) {
        this.key = key;
        MainCommand.registerSubCommand(this);
    }

    public abstract void onIntialize(String[] args, CommandSender sender, Command command);

    public abstract List<String> getTabCompleter(String[] args, CommandSender sender, Command command);

    public final void intialize(String[] args, CommandSender sender, Command command) {
        onIntialize(args, sender, command);
    }

    public String getKey() {
        return key;
    }
}
