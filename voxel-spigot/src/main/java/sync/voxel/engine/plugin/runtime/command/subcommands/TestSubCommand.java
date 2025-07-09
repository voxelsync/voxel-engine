package sync.voxel.engine.plugin.runtime.command.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import sync.voxel.engine.api.material.VoxMaterial;
import sync.voxel.engine.plugin.runtime.command.SubCommand;
import sync.voxel.engine.plugin.runtime.enchantment.VoxelEnchantment;
import sync.voxel.engine.plugin.utils.item.VoxelItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TestSubCommand extends SubCommand {

    public TestSubCommand(String... key) {
        super(key);
        registerDefaultActions();
    }

    private final Map<String, Consumer<Player>> actions = new HashMap<>();

    public void registerAction(@NotNull String key, Consumer<Player> action) {
        actions.put(key.toLowerCase(), action);
    }

    private void registerDefaultActions() {
        registerAction("test_block", player -> {
            ItemStack block = createTestBlock();
            player.getInventory().addItem(block);
            player.sendMessage("§aDu hast einen Test-Block erhalten!");
        });

        registerAction("get_lore", player -> {
            ItemStack block = createTestBlock();
            List<Component> lore = player.getActiveItem().lore() != null ? player.getActiveItem().lore() : new ArrayList<>();
            assert lore != null;
            player.sendMessage("ItemLore[");
            lore.forEach(line -> player.sendMessage(line.append(Component.text(","))));
            player.sendMessage("]");
        });

        registerAction("explode", player -> {
            player.getWorld().createExplosion(player.getLocation(), 2.0f, false);
            player.sendMessage("§cBoom!");
        });
        registerAction("listenchants", player -> {
            player.sendMessage("Size:" + VoxelEnchantment.values().size());
            VoxelEnchantment.values().forEach(enchant -> player.sendMessage(enchant.getKey().toString()));
        });
    }

    private @NotNull ItemStack createTestBlock() {
        return VoxelItem.edit(new ItemStack(Material.PAPER)).setVoMaterial(VoxMaterial.valueOf("voxel:test_block")).toNewItemStack();
    }


    @Override
    public void onIntialize(String[] args, CommandSender sender, Command command) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen!");
            return;
        }

        if (args.length == 0) {
            player.sendMessage("§6Verfügbare Keys: " + String.join(", ", actions.keySet()));
            return;
        }

        String key = args[0].toLowerCase();
        Consumer<Player> action = actions.get(key);

        if (action != null) {
            action.accept(player);
        } else {
            player.sendMessage("§cUnbekannter Key: " + key);
        }
    }

    @Override
    public List<String> getTabCompleter(String[] args, CommandSender sender, Command command) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            for (String string : actions.keySet()) {
                list.add(string);
            }
        }
        return list;
    }

}
