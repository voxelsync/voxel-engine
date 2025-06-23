package sync.voxel.paper.runtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sync.voxel.paper.runtime.command.subcommands.EnchantSubCommand;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {
    
    public List<SubCommand> subCommands = new ArrayList<>();
    
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        for (SubCommand subCommand : subCommands) {
            
        }
        return false;
    }

    public void registerSubCommand(SubCommand command) {
        subCommands.add(command);
    }

    public void registerSubCommands() {
        registerSubCommand(new EnchantSubCommand("enchant"));
    }


}
