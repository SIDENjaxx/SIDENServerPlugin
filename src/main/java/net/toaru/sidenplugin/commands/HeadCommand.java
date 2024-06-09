package net.toaru.sidenplugin.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.toaru.sidenplugin.Main;
import net.toaru.sidenplugin.utils.URLUtils;
import net.toaru.sidenplugin.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class HeadCommand implements CommandExecutor, TabCompleter
{
    private static void getHead(Player recipient, String name)
    {
        recipient.sendMessage(ChatColor.DARK_RED + name + " を探しています...");

        Player target = Utils.getPlayerAllowOffline(name);

        String uuid;
        String playerName;

        if (target == null)
        {
            recipient.sendMessage(ChatColor.RED + "おっと！ " + name + " という名前のプレイヤーは, このサーバに一度もログインしたことがないようです。\n" +
                    ChatColor.RED + "Minecraft サーバに問い合わせています...");

            String uu = URLUtils.getAsString("https://api.mojang.com/users/profiles/minecraft/" + name);
            if (uu.startsWith("E: "))
            {
                recipient.sendMessage(ChatColor.RED + "エラー：" + uu.substring(3));
                return;
            }

            JsonObject object = new Gson().fromJson(uu, JsonObject.class);

            uuid = object.get("id").getAsString();
            playerName = object.get("name").getAsString();

        }
        else
        {
            uuid = target.getUniqueId().toString().replace("-", "");
            playerName = target.getName();
        }

        recipient.sendMessage(ChatColor.GREEN + "プレイヤー" + playerName + "の UUID を取得しました。");

        recipient.sendMessage(ChatColor.DARK_RED + "プレイヤー" + playerName + "の頭を切り取っています...");

        String skinResult = URLUtils.getAsString("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
        if (skinResult.startsWith("E: "))
        {
            recipient.sendMessage(ChatColor.RED + "エラー：" + skinResult.substring(3));
            return;
        }

        JsonObject object = new Gson().fromJson(skinResult, JsonObject.class);

        JsonArray properties = object.get("properties").getAsJsonArray();

        JsonObject texture = null;

        for (JsonElement elm : properties)
        {
            JsonObject obj = (JsonObject) elm;
            if (!obj.get("name").getAsString().equals("textures"))
                continue;
            texture = new Gson().fromJson(
                    new String(Base64.getDecoder().decode(obj.get("value").getAsString())),
                    JsonObject.class
            );
            break;
        }

        if (texture == null)
        {
            recipient.sendMessage(ChatColor.RED + "エラー：プレイヤーが見つかりませんでした！");
            return;
        }

        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);

        try
        {
            String url = texture.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
            byte[] skin = ("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes();

            Bukkit.getUnsafe().modifyItemStack(
                    stack,
                    "{SkullOwner:{Id:\"" + new UUID(uuid.hashCode(), uuid.hashCode()) + "\",Properties:{textures:[{Value:\"" + Base64.getEncoder().encodeToString(skin) + "\"}]}}}"
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
            recipient.sendMessage(ChatColor.RED + "エラーが発生しました：" + e.getClass());
            return;
        }
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.displayName(Component.text(playerName + "の頭"));

        meta.lore(Collections.singletonList(Component.text(ChatColor.DARK_RED + "くびちょんぱ！")));
        stack.setItemMeta(meta);
        recipient.getInventory().addItem(stack);
        recipient.sendMessage(ChatColor.GREEN + playerName + "の頭を手に入れました。");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!Utils.hasPermission(sender, "lighthead.head") ||
                !Utils.isPlayer(sender) ||
                !Utils.invalidLengthMessage(sender, args, 1, 1))
            return true;

        Player player = (Player) sender;


        sender.sendMessage(ChatColor.DARK_RED + "頭を生成中...");
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                getHead(player, args[0]);
            }
        }.runTaskAsynchronously(Main.getPlugin());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        ArrayList<String> tab = new ArrayList<>();

        if (args.length == 1)
        {
            tab.add(args[0]);
            tab.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName).collect(Collectors.toList()));
            tab.addAll(Arrays.stream(Bukkit.getOfflinePlayers())
                    .map(OfflinePlayer::getName)
                    .filter(op -> !tab.contains(op))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        ArrayList<String> asCopy = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], tab, asCopy);
        Collections.sort(asCopy);
        return asCopy;

    }
}