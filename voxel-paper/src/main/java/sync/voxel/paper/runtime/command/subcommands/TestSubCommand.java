package sync.voxel.paper.runtime.command.subcommands;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import sync.voxel.paper.runtime.command.SubCommand;
import sync.voxel.paper.runtime.material.VoxelMaterial;

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

        registerAction("explode", player -> {
            player.getWorld().createExplosion(player.getLocation(), 2.0f, false);
            player.sendMessage("§cBoom!");
        });
    }

    private @NotNull ItemStack createTestBlock() {
        ItemStack block = new ItemStack(Material.PAPER);
        NamespacedKey key = new NamespacedKey("voxelmeta", "material");

        block.editMeta(meta -> {
            meta.getPersistentDataContainer().set(
                    key,
                    PersistentDataType.STRING,
                    "voxel:test_block"
            );
            meta.setCustomModelData(187);
        });

        return block;
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
        return new ArrayList<>();
    }

}
