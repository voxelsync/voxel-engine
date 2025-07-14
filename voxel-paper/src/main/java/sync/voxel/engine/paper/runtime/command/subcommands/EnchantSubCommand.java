package sync.voxel.engine.paper.runtime.command.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sync.voxel.engine.api.enchantment.VoxEnchantment;
import sync.voxel.engine.paper.runtime.command.SubCommand;
import sync.voxel.engine.paper.runtime.registry.enchantment.VoxelEnchantment;
import sync.voxel.engine.paper.utils.ConvertUtils;
import sync.voxel.engine.paper.utils.voxel.VoxelItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sync.voxel.engine.paper.PaperPlugin.prefix;

public class EnchantSubCommand extends SubCommand {

    @Override
    public void execute(String[] args, CommandSender sender, Command command) {
        if (args.length < 1) {
            sender.sendMessage(prefix.append(Component.text("§cYou have to specify a Action <- exception in argument 2")));
            return;
        }
        VoxEnchantment voEnchant;
        Integer lvl;
        Player player;
        switch (args[0].toLowerCase()) {
            case "getbook":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to be a Player for this action")));
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment <- exception in argument 3")));
                    return;
                }
                if (VoxEnchantment.valueOf(args[1]) == null) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Enchantment <- exception in argument 3")));
                    return;
                }
                voEnchant = VoxEnchantment.valueOf(args[1]);
                if (args.length < 3) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment Level <- exception in argument 4")));
                    return;
                }
                if (ConvertUtils.getInt(args[2]).isEmpty()) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Number <- exception in argument 4")));
                    return;
                }
                lvl = ConvertUtils.getInt(args[2]).orElse(0);
                player = (Player) sender;
                player.getInventory().addItem(getBook(voEnchant, lvl > 255 ? 255 : lvl));
                break;

            case "add":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to be a Player for this action")));
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment <- exception in argument 3")));
                    return;
                }
                if (VoxEnchantment.valueOf(args[1]) == null) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Enchantment <- exception in argument 3")));
                    return;
                }
                voEnchant = VoxEnchantment.valueOf(args[1]);
                if (args.length < 3) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment Level <- exception in argument 4")));
                    return;
                }
                if (ConvertUtils.getInt(args[2]).isEmpty()) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Number <- exception in argument 4")));
                    return;
                }
                lvl = ConvertUtils.getInt(args[2]).orElse(0);
                player = (Player) sender;
                VoxelItem.edit(player.getInventory().getItemInMainHand()).addEnchant(voEnchant, lvl);
                break;
            case "remove":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to be a Player for this action")));
                    return;
                }
                player = (Player) sender;
                if (args.length < 2) {
                    sender.sendMessage(prefix.append(Component.text("§cYou have to specify an Enchantment <- exception in argument 3")));
                    return;
                }
                if (VoxEnchantment.valueOf(args[1]) == null) {
                    sender.sendMessage(prefix.append(Component.text("§cInvalid Enchantment <- exception in argument 3")));
                    return;
                }
                voEnchant = VoxEnchantment.valueOf(args[1]);
                if (VoxelItem.edit(player.getInventory().getItemInMainHand()).hasEnchant(voEnchant)) {
                    sender.sendMessage(prefix.append(Component.text("§cTYour Item doesn't have this Enchantment")));
                    return;
                }
                VoxelItem.edit(player.getInventory().getItemInMainHand()).removeEnchant(voEnchant);
                break;
            default:
                sender.sendMessage(prefix.append(Component.text("§cInvalid Action <- Exception in Argument 2")));
                break;
        }

    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender, Command command) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("getBook");
            list.add("add");
            list.add("remove");
        }
        if (args.length == 2) {
            if (Arrays.asList("getbook", "add", "remove").contains(args[0].toLowerCase())) {
                VoxelEnchantment.values().forEach(enchant -> list.add(enchant.getKey().toString()));
            }
        }
        if (args.length == 3) {
            if (Arrays.asList("getbook", "add").contains(args[0].toLowerCase())) {
                if (VoxEnchantment.valueOf(args[1]) != null) {
                    for (int i = 0; i < VoxEnchantment.valueOf(args[1]).getAttribute("max_level", Integer.class, 1)+1; i++) {
                        list.add(Integer.toString(i));
                    }
                }
            }
        }
        return list;
    }

    public EnchantSubCommand(String... key) {
        super(key);
    }

    public ItemStack getBook(VoxEnchantment enchant, Integer level) {
        return VoxelItem.edit(new ItemStack(Material.ENCHANTED_BOOK)).addEnchant(enchant, level).stack();
    }
}
