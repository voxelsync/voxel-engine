package sync.voxel.paper.runtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sync.voxel.paper.runtime.command.subcommands.EnchantSubCommand;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return false;
    }

    public void registerSubCommand(SubCommand command) {

    }

    public void registerSubCommands() {
        registerSubCommand(new EnchantSubCommand("enchant"));
    }


}
