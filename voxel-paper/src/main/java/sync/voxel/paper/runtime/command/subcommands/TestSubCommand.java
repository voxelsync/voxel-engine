package sync.voxel.paper.runtime.command.subcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import sync.voxel.paper.runtime.command.SubCommand;

import java.util.List;

public class TestSubCommand extends SubCommand {

    @Override
    public void onIntialize(String[] args, CommandSender sender, Command command) {

    }

    @Override
    public List<String> getTabCompleter(String[] args, CommandSender sender, Command command) {

    }

    public TestSubCommand(String key) {
        super(key);
    }
}
