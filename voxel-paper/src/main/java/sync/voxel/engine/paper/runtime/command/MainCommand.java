package sync.voxel.engine.paper.runtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import sync.voxel.engine.paper.PaperPlugin;
import sync.voxel.engine.paper.runtime.command.subcommands.EnchantSubCommand;
import sync.voxel.engine.paper.runtime.command.subcommands.TestSubCommand;
import sync.voxel.engine.paper.utils.text.Label;

import java.util.*;

public final class MainCommand extends Command {
    
    public static HashMap<String, SubCommand> subCommands;

    public MainCommand() {
        super("voxelengine", "A Command to manage VoxelEngine", "/", Arrays.stream(new String[]{"ve", "voxel", "vo"}).toList());
    }

    public static void registerSubCommand(SubCommand command) {
        subCommands.put(command.getKey(), command);
    }

    public static void register() {
        MainCommand mainCommand = new MainCommand();
        mainCommand.registerSubCommands();
        mainCommand.register(PaperPlugin.plugin.getServer().getCommandMap());
    }

    public void registerSubCommands() {
        subCommands = new HashMap<>();
        new EnchantSubCommand("enchant");
        new TestSubCommand("test", "dev");
    }


    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
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
                list.addAll(subCommand.tabComplete(argList.toArray(String[]::new), sender, this));
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

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String command, @NotNull String @NotNull [] args) {
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
        subCommands.get(args[0]).execute(argList.toArray(String[]::new), sender, this);
        return false;
    }
}
