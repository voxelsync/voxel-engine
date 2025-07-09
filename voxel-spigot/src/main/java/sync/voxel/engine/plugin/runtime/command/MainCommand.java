package sync.voxel.engine.plugin.runtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sync.voxel.engine.plugin.runtime.command.subcommands.EnchantSubCommand;
import sync.voxel.engine.plugin.runtime.command.subcommands.TestSubCommand;
import sync.voxel.engine.plugin.utils.text.Label;

import java.util.*;

public class MainCommand implements CommandExecutor, TabCompleter {
    
    public static HashMap<String, SubCommand> subCommands;
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sender.sendMessage(Label.of("command.main.", "error.no_subcommand").toTranslation().toComponent());
            return true;
        }
        if (!subCommands.containsKey(args[0])) {
            sender.sendMessage(Label.of("command.main.", "error.invalid_subcommand").toTranslation().toComponent());
            return true;
        }
        List<String> argList = new ArrayList<>(Arrays.asList(args));
        argList.removeFirst();
        subCommands.get(args[0]).intialize(argList.toArray(String[]::new), sender, command);
        return false;
    }

    public static void registerSubCommand(SubCommand command) {
        subCommands.put(command.getKey(), command);
    }

    public void registerSubCommands() {
        subCommands = new HashMap<>();
        new EnchantSubCommand("enchant");
        new TestSubCommand("test", "dev");
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (SubCommand subCommand : subCommands.values()) {
                list.add(subCommand.getKey());
            }
        }
        if (args.length > 1) {
            List<String> argList = new ArrayList<>(Arrays.asList(args));
            argList.removeFirst();
            if (subCommands.containsKey(args[0].toLowerCase())) {
                SubCommand subCommand = subCommands.get(args[0].toLowerCase());
                for (String tabComplete : subCommand.getTabCompleter(argList.toArray(String[]::new), sender, command)) {
                    list.add(tabComplete);
                }
            }
        }
        ArrayList<String> completerlist = new ArrayList<>();
        String arg = args[args.length-1].toLowerCase();
        for (String string : list) {
            String s1 = string.toLowerCase();
            if (s1.toLowerCase().startsWith(arg)) {
                completerlist.add(string);
            }
        }
        return completerlist;
    }
}
