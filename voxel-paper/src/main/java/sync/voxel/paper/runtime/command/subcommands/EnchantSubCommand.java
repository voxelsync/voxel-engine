package sync.voxel.paper.runtime.command.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sync.voxel.api.enchantment.VoEnchantment;
import sync.voxel.paper.PaperPlugin;
import sync.voxel.paper.runtime.command.SubCommand;

import java.util.ArrayList;
import java.util.List;

import static sync.voxel.paper.PaperPlugin.prefix;
import static sync.voxel.paper.runtime.enchantment.VoxelEnchantManager.voxEnchantManager;

public class EnchantSubCommand extends SubCommand {

    @Override
    public void onIntialize(String[] args, CommandSender sender, Command command) {
        if (args.length < 1) {
            sender.sendMessage(prefix + "§cYou have to specify a Action <- exception in argument 1");
            return;
        }
        VoEnchantment voEnchant;
        Integer lvl;
        Player player;
        switch (args[0].toLowerCase()) {
            case "getBook":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to be a Player for this action")));
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment <- exception in argument 2")));
                    return;
                }
                if (VoEnchantment.valueOf(args[1]) == null) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Enchantment <- exception in argument 2")));
                    return;
                }
                voEnchant = VoEnchantment.valueOf(args[1]);
                if (args.length < 3) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment Level <- exception in argument 3")));
                    return;
                }
                if (PaperPlugin.getInt(args[2]) == null) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Number <- exception in argument 3")));
                    return;
                }
                lvl = PaperPlugin.getInt(args[2]);
                player = (Player) sender;
                player.getInventory().addItem(getBook(voEnchant, lvl > 255 ? 255 : lvl));
                break;

            case "add":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to be a Player for this action")));
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment <- exception in argument 2")));
                    return;
                }
                if (VoEnchantment.valueOf(args[1]) == null) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Enchantment <- exception in argument 2")));
                    return;
                }
                voEnchant = VoEnchantment.valueOf(args[1]);
                if (args.length < 3) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment Level <- exception in argument 3")));
                    return;
                }
                if (PaperPlugin.getInt(args[2]) == null) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Number <- exception in argument 3")));
                    return;
                }
                lvl = PaperPlugin.getInt(args[2]);
                player = (Player) sender;
                voxEnchantManager.addEnchant(player.getInventory().getItemInMainHand(), voEnchant, lvl);
                break;
        }

    }

    @Override
    public List<String> getTabCompleter(String[] args, CommandSender sender, Command command) {
        List<String> list = new ArrayList<>();
        list.add("getBook");
        list.add("add");
        return list;
    }

    public EnchantSubCommand(String... key) {
        super(key);
    }

    public ItemStack getBook(VoEnchantment enchant, Integer level) {
        return voxEnchantManager.addEnchant(new ItemStack(Material.ENCHANTED_BOOK), enchant, level);
    }
}
