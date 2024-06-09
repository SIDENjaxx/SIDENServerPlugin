package net.toaru.sidenplugin.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.toaru.sidenplugin.utils.Mention;
import net.toaru.sidenplugin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionEventListener implements Listener {
    private final String _PREFIX;

    private String regex;
    private Pattern pattern;

    public MentionEventListener(Plugin plugin) {
        _PREFIX = plugin.getConfig().getString("mention.prefix");
        regex = Utils.escapeRegex(Objects.requireNonNull(_PREFIX)) + "()";
    }

    private boolean a(String text) {
        return (regex.contains("(" + text) || regex.contains("|" + text)) && (regex.contains(text + ")") || regex.contains(text + "|"));
    }

    private void b(String text) {
        regex = regex.replace("(" + text + "|", "(");
        regex = regex.replace("(" + text + ")", "()");
        regex = regex.replace("|" + text + "|", "|");
        regex = regex.replace("|" + text + ")", ")");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        b(player.getName());
        b(player.getUniqueId().toString());
        b(player.getUniqueId().toString().replace("-", ""));
        if (player.getCustomName() != null)
            b(player.getCustomName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPermission("seikatsu.mention.bypass"))
            return;

        if (a(player.getName()) ||
                a(((TextComponent) player.displayName()).content()) ||
                a(player.getUniqueId().toString()) ||
                a(player.getUniqueId().toString().replace("-", "")))
            return;

        regex = regex.substring(0, regex.length() - 1) + (regex.length() == _PREFIX.length() + 2 ? "" : "|") + Utils.escapeRegex(player.getName()) + ")";
        regex = regex.substring(0, regex.length() - 1) + "|" + player.getUniqueId() + ")";
        regex = regex.substring(0, regex.length() - 1) + "|" + player.getUniqueId().toString().replace("-", "") + ")";

        if (player.getCustomName() != null)
            regex = regex.substring(0, regex.length() - 1) + "|" + Utils.escapeRegex(((TextComponent) player.displayName()).content()) + ")";

        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncChatEvent e) {
        if (pattern == null)
            return;

        if (!e.getPlayer().hasPermission("seikatsu.mention.use"))
            return;

        TextComponent textComponent = (TextComponent) e.message();
        String text = textComponent.content();

        List<Player> players = getMentionPlayers(text);

        if (players.size() == 0)
            return;

        e.setCancelled(true); // 元のイベントをキャンセルしてメッセージのブロードキャストを防ぐ

        // メンションされたプレイヤーに手動でメッセージを送信
        for (Player player : players) {
            String body = getText(text, player, players);
            player.sendMessage("<" + e.getPlayer().getName() + "> " + body);
            Mention.onMention(player, e.getPlayer(), body);
        }

        // メンションされなかったプレイヤーに元のメッセージを送信
        for (Player recipient : Bukkit.getOnlinePlayers()) {
            if (!players.contains(recipient)) {
                recipient.sendMessage(textComponent);
            }
        }
    }

    public String getText(String text, Player target, List<Player> players) {
        String body = null;

        Matcher matcher = pattern.matcher(text);

        for (Player player : players) {
            boolean isTarget = player.getUniqueId().equals(target.getUniqueId());
            body = matcher.replaceAll((isTarget ? ChatColor.GOLD : ChatColor.AQUA) + (isTarget ? ChatColor.ITALIC.toString() : "") + "$0" + ChatColor.RESET);
        }

        return body;
    }

    public List<Player> getMentionPlayers(String text) {
        Matcher matcher = pattern.matcher(text);

        List<Player> players = new ArrayList<>();

        while (matcher.find()) {
            Player player;
            String name = matcher.group(1);

            player = Bukkit.getPlayer(name);

            if (player != null) {
                players.add(player);
                continue;
            }

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (((TextComponent) p.displayName()).content().equals(name))
                    players.add(p);
            });
        }

        return players;
    }
}
