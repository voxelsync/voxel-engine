package sync.voxel.paper.runtime.command.subcommands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import sync.voxel.api.enchantment.VoEnchantment;
import sync.voxel.paper.runtime.command.SubCommand;

import static sync.voxel.paper.PaperPlugin.prefix;
import static sync.voxel.paper.runtime.enchantment.VoxelEnchantManager.voxEnchantManager;

public class EnchantSubCommand extends SubCommand {

    @Override
    public void onIntialize(String[] args, CommandSender sender, Command command) {
        if (args.length < 1) {
            sender.sendMessage(prefix + "§cYou have to specify a Action <- exception in argument 1");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "getBook":
                if (args.length < 2) {
                    sender.sendMessage(prefix + "§cYou have to specify a Enchantment <- exception in argument 1");
                    return;
                }
                VoEnchantment.valueOf(args[1]);

        }
    }

    @Override
    public void getTabCompleter(String[] args, CommandSender sender, Command command) {
    }

    public EnchantSubCommand(String key) {
        super(key);
    }

    public ItemStack getBook(VoEnchantment enchant, Integer level) {
        return voxEnchantManager.addEnchant(new ItemStack(Material.ENCHANTED_BOOK), enchant, level);
    }
}
