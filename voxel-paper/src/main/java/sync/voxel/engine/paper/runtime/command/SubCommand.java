package sync.voxel.engine.paper.runtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

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

    public abstract void execute(String[] args, CommandSender sender, Command command);

    public abstract List<String> tabComplete(String[] args, CommandSender sender, Command command);

    public String getKey() {
        return key;
    }
}
