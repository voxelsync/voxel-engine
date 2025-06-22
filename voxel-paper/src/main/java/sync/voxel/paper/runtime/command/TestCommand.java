package sync.voxel.paper.runtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import sync.voxel.paper.runtime.material.VoxelMaterial;

import javax.sound.midi.SysexMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TestCommand implements CommandExecutor {
    private final Map<String, Consumer<Player>> actions = new HashMap<>();

    public TestCommand() {
        registerDefaultActions();
    }

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
        ItemStack block = new ItemStack(VoxelMaterial.valueOf("voxel:test_block").getVaMaterial());
        NamespacedKey key = new NamespacedKey("voxelmeta", "material");

        block.editMeta(meta -> {
            meta.getPersistentDataContainer().set(
                    key,
                    PersistentDataType.STRING,
                    "voxel:test_block"
            );
        });

        return block;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl nutzen!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§6Verfügbare Keys: " + String.join(", ", actions.keySet()));
            return true;
        }

        String key = args[0].toLowerCase();
        Consumer<Player> action = actions.get(key);

        if (action != null) {
            action.accept(player);
        } else {
            player.sendMessage("§cUnbekannter Key: " + key);
        }

        return true;
    }
}