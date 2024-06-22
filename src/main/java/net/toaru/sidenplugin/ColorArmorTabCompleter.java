package net.toaru.sidenplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class ColorArmorTabCompleter implements TabCompleter {
    private static final List<String> COLORS = Arrays.asList("白", "銀", "灰", "黒", "赤", "栗", "黄", "オリーブ", "ライム", "緑", "水", "青緑", "青", "紺", "ピンク", "紫");
    private static final List<String> ARMOR_TYPES = Arrays.asList("ヘルメット", "チェストプレート", "レギンス", "ブーツ");
    private static final Map<String, Enchantment> ENCHANTMENTS = Map.ofEntries(
            entry("保護", Enchantment.PROTECTION_ENVIRONMENTAL),
            entry("火炎耐性", Enchantment.PROTECTION_FIRE),
            entry("落下耐性", Enchantment.PROTECTION_FALL),
            entry("爆発耐性", Enchantment.PROTECTION_EXPLOSIONS),
            entry("飛び道具耐性", Enchantment.PROTECTION_PROJECTILE),
            entry("水中呼吸", Enchantment.OXYGEN),
            entry("水中採掘", Enchantment.WATER_WORKER),
            entry("棘の鎧", Enchantment.THORNS),
            entry("水中歩行", Enchantment.DEPTH_STRIDER),
            entry("氷渡り", Enchantment.FROST_WALKER),
            entry("束縛の呪い", Enchantment.BINDING_CURSE),
            entry("消滅の呪い", Enchantment.VANISHING_CURSE)
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return getMatches(args[0], COLORS);
        } else if (args.length == 2) {
            return getMatches(args[1], ARMOR_TYPES);
        } else if (args.length == 4) {
            return getMatches(args[3], new ArrayList<>(ENCHANTMENTS.keySet()));
        }
        return new ArrayList<>();
    }

    private List<String> getMatches(String prefix, List<String> options) {
        return options.stream()
                .filter(option -> option.toLowerCase().startsWith(prefix.toLowerCase()))
                .collect(Collectors.toList());
    }
}
