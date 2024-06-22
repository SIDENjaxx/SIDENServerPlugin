package net.toaru.sidenplugin;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ColorArmorCommand implements CommandExecutor {
    private final Plugin plugin;

    // サポートする色のマッピング
    private static final Map<String, Color> COLORS = new HashMap<>();
    static {
        COLORS.put("白", Color.WHITE);
        COLORS.put("銀", Color.SILVER);
        COLORS.put("灰", Color.GRAY);
        COLORS.put("黒", Color.BLACK);
        COLORS.put("赤", Color.RED);
        COLORS.put("栗", Color.MAROON);
        COLORS.put("黄", Color.YELLOW);
        COLORS.put("オリーブ", Color.OLIVE);
        COLORS.put("ライム", Color.LIME);
        COLORS.put("緑", Color.GREEN);
        COLORS.put("水", Color.AQUA);
        COLORS.put("青緑", Color.TEAL);
        COLORS.put("青", Color.BLUE);
        COLORS.put("紺", Color.NAVY);
        COLORS.put("ピンク", Color.FUCHSIA);
        COLORS.put("紫", Color.PURPLE);
    }

    // サポートするアーマーの種類のマッピング
    private static final Map<String, Material> ARMOR_TYPES = new HashMap<>();
    static {
        ARMOR_TYPES.put("ヘルメット", Material.LEATHER_HELMET);
        ARMOR_TYPES.put("チェストプレート", Material.LEATHER_CHESTPLATE);
        ARMOR_TYPES.put("レギンス", Material.LEATHER_LEGGINGS);
        ARMOR_TYPES.put("ブーツ", Material.LEATHER_BOOTS);
    }

    // エンチャント名のマッピング
    private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<>();
    static {
        ENCHANTMENTS.put("保護", Enchantment.PROTECTION_ENVIRONMENTAL);
        ENCHANTMENTS.put("火炎耐性", Enchantment.PROTECTION_FIRE);
        ENCHANTMENTS.put("落下耐性", Enchantment.PROTECTION_FALL);
        ENCHANTMENTS.put("爆発耐性", Enchantment.PROTECTION_EXPLOSIONS);
        ENCHANTMENTS.put("飛び道具耐性", Enchantment.PROTECTION_PROJECTILE);
        ENCHANTMENTS.put("水中呼吸", Enchantment.OXYGEN);
        ENCHANTMENTS.put("水中採掘", Enchantment.WATER_WORKER);
        ENCHANTMENTS.put("棘の鎧", Enchantment.THORNS);
        ENCHANTMENTS.put("水中歩行", Enchantment.DEPTH_STRIDER);
        ENCHANTMENTS.put("氷渡り", Enchantment.FROST_WALKER);
        ENCHANTMENTS.put("束縛の呪い", Enchantment.BINDING_CURSE);
        ENCHANTMENTS.put("消滅の呪い", Enchantment.VANISHING_CURSE);
    }

    public ColorArmorCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみが使用できます。");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "使用方法: /colorarmor <色> <アーマーの種類> [<耐久値>] [<エンチャント>:<レベル>]");
            return true;
        }

        Player player = (Player) sender;
        String colorName = args[0];
        String armorType = args[1];

        Color color = COLORS.get(colorName);
        Material armorMaterial = ARMOR_TYPES.get(armorType);

        if (color == null) {
            player.sendMessage(ChatColor.RED + "無効な色です。使用可能な色: " + String.join(", ", COLORS.keySet()));
            return true;
        }

        if (armorMaterial == null) {
            player.sendMessage(ChatColor.RED + "無効なアーマーの種類です。使用可能な種類: " + String.join(", ", ARMOR_TYPES.keySet()));
            return true;
        }

        ItemStack armor = new ItemStack(armorMaterial);
        LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        meta.setColor(color);

        // 耐久値の設定
        if (args.length >= 3) {
            try {
                int durability = Integer.parseInt(args[2]);
                armor.setDurability((short) durability);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "無効な耐久値です。数値を入力してください。");
                return true;
            }
        }

        // エンチャントの設定
        if (args.length >= 4) {
            String[] enchantmentArgs = args[3].split(":");
            if (enchantmentArgs.length == 2) {
                try {
                    Enchantment enchantment = ENCHANTMENTS.get(enchantmentArgs[0]);
                    int level = Integer.parseInt(enchantmentArgs[1]);
                    if (enchantment != null) {
                        armor.addEnchantment(enchantment, level);
                    } else {
                        player.sendMessage(ChatColor.RED + "無効なエンチャントです。使用可能なエンチャント: " + String.join(", ", ENCHANTMENTS.keySet()));
                        return true;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "無効なエンチャントレベルです。数値を入力してください。");
                    return true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "エンチャントの形式が無効です。<エンチャント>:<レベル>の形式で入力してください。");
                return true;
            }
        }

        armor.setItemMeta(meta);
        player.getInventory().addItem(armor);
        player.sendMessage(ChatColor.GREEN + colorName + "色の" + armorType + "を与えました。");

        return true;
    }
}
