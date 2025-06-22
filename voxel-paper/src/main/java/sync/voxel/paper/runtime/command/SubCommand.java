package sync.voxel.paper.runtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

public abstract class SubCommand {

    private final String key;

    public SubCommand(String key) {
        this.key = key;
    }

    public abstract void onIntialize(String[] args, CommandSender sender, Command command);

    public abstract void getTabCompleter(String[] args, CommandSender sender, Command command);
}
