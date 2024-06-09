package net.toaru.sidenplugin;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ItemEdit implements CommandExecutor, TabCompleter {

    private final Logger logger;

    public ItemEdit(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ使用できます。");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            player.sendMessage("エラー: メインハンドにアイテムを持っていません。");
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            meta = item.getItemMeta();
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            displayHelp(player);
            return true;
        }

        boolean result;
        switch (args[0].toLowerCase()) {
            case "name":
                result = handleNameCommand(player, item, meta, args);
                break;
            case "lore":
                result = handleLoreCommand(player, item, meta, args);
                break;
            case "addlore":
                result = handleAddLoreCommand(player, item, meta, args);
                break;
            case "enchant":
                result = handleEnchantCommand(player, item, meta, args);
                break;
            case "removeenchant":
                result = handleRemoveEnchantCommand(player, item, meta, args);
                break;
            case "listenchants":
                result = handleListEnchantsCommand(player, item, meta);
                break;
            case "listallenchants":
                result = handleListAllEnchantsCommand(player);
                break;
            default:
                player.sendMessage("エラー: 不明なコマンドです。");
                displayHelp(player);
                result = false;
                break;
        }

        logger.info("Player " + player.getName() + " executed command: " + String.join(" ", args) + " with result: " + (result ? "success" : "failure"));
        return result;
    }

    private void displayHelp(Player player) {
        player.sendMessage("使用可能なコマンド:");
        player.sendMessage("/itemedit name <名前> - アイテムの名前を変更します");
        player.sendMessage("/itemedit lore <説明> - アイテムの説明を変更します");
        player.sendMessage("/itemedit addlore <説明> - アイテムの説明を追加します");
        player.sendMessage("/itemedit enchant <エンチャント名> <レベル> - アイテムにエンチャントを追加します");
        player.sendMessage("/itemedit removeenchant <エンチャント名> - アイテムからエンチャントを削除します");
        player.sendMessage("/itemedit listenchants - アイテムのエンチャントを表示します");
        player.sendMessage("/itemedit listallenchants - インベントリ内のすべてのアイテムのエンチャントを表示します");
        player.sendMessage("/itemedit help - このヘルプメッセージを表示します");
        player.sendMessage("名前や説明に色を付ける場合は、&を使って色コードを指定してください。例: &6黄金の剣");
    }

    private boolean handleNameCommand(Player player, ItemStack item, ItemMeta meta, String[] args) {
        if (args.length > 1) {
            String name = ChatColor.translateAlternateColorCodes('&', String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            player.sendMessage("アイテムの名前が変更されました: " + name);
            return true;
        } else {
            player.sendMessage("エラー: 名前を指定してください。");
            return false;
        }
    }

    private boolean handleLoreCommand(Player player, ItemStack item, ItemMeta meta, String[] args) {
        if (args.length > 1) {
            List<String> lore = Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.sendMessage("アイテムの説明が変更されました: " + String.join(", ", lore));
            return true;
        } else {
            player.sendMessage("エラー: 説明を指定してください。");
            return false;
        }
    }

    private boolean handleAddLoreCommand(Player player, ItemStack item, ItemMeta meta, String[] args) {
        if (args.length > 1) {
            List<String> lore = meta.hasLore() ? meta.getLore() : Collections.emptyList();
            lore.addAll(Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList()));
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.sendMessage("アイテムの説明が追加されました: " + String.join(", ", lore));
            return true;
        } else {
            player.sendMessage("エラー: 追加する説明を指定してください。");
            return false;
        }
    }

    private boolean handleEnchantCommand(Player player, ItemStack item, ItemMeta meta, String[] args) {
        if (args.length > 2) {
            try {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[1].toLowerCase()));
                int level = Integer.parseInt(args[2]);
                if (enchantment != null && level > 0 && level <= enchantment.getMaxLevel()) {
                    meta.addEnchant(enchantment, level, true);
                    item.setItemMeta(meta);
                    player.sendMessage("アイテムにエンチャントが追加されました: " + enchantment.getKey().getKey() + " レベル " + level);
                    return true;
                } else {
                    player.sendMessage("エラー: 無効なエンチャント名またはレベルです。");
                    return false;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("エラー: エンチャントレベルが無効です。");
                return false;
            }
        } else {
            player.sendMessage("エラー: エンチャント名とレベルを指定してください。");
            return false;
        }
    }

    private boolean handleRemoveEnchantCommand(Player player, ItemStack item, ItemMeta meta, String[] args) {
        if (args.length > 1) {
            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[1].toLowerCase()));
            if (enchantment != null && meta.hasEnchant(enchantment)) {
                meta.removeEnchant(enchantment);
                item.setItemMeta(meta);
                player.sendMessage("エンチャントが削除されました: " + enchantment.getKey().getKey());
                return true;
            } else {
                player.sendMessage("エラー: エンチャントが見つかりません。");
                return false;
            }
        } else {
            player.sendMessage("エラー: 削除するエンチャント名を指定してください。");
            return false;
        }
    }

    private boolean handleListEnchantsCommand(Player player, ItemStack item, ItemMeta meta) {
        if (meta.hasEnchants()) {
            player.sendMessage("現在のエンチャント:");
            meta.getEnchants().forEach((enchantment, level) ->
                    player.sendMessage(enchantment.getKey().getKey() + " レベル " + level));
            return true;
        } else {
            player.sendMessage("このアイテムにはエンチャントがありません。");
            return true;
        }
    }

    private boolean handleListAllEnchantsCommand(Player player) {
        player.sendMessage("インベントリ内のアイテムのエンチャント:");
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
                player.sendMessage("アイテム: " + item.getType().name());
                item.getItemMeta().getEnchants().forEach((enchantment, level) ->
                        player.sendMessage("  " + enchantment.getKey().getKey() + " レベル " + level));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("name", "lore", "addlore", "enchant", "removeenchant", "listenchants", "listallenchants", "help");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("removeenchant"))) {
            return Arrays.stream(Enchantment.values())
                    .map(enchantment -> enchantment.getKey().getKey())
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
