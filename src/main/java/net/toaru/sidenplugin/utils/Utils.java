package net.toaru.sidenplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class Utils
{
    public static String escapeRegex(String text)
    {
        return text.replace("\\", "\\\\")
                .replace("*", "\\*")
                .replace("+", "\\+")
                .replace(".", "\\.")
                .replace("?", "\\?")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("[", "\\]")
                .replace("^", "\\^")
                .replace("$", "\\$")
                .replace("-", "\\-")
                .replace("|", "\\|");
    }

    public static Location getRandomLocationWithoutFilled(Location center, int range)
    {
        Random random = new Random();
        Location loc = center.clone().add(random.nextInt(range), random.nextInt(range), random.nextInt(range));
        return loc.getWorld().getHighestBlockAt(loc).getLocation();
    }

    public static LivingEntity getLookingEntity(Player player)
    {
        for (Location location : player.getLineOfSight(null, 4).parallelStream().map(Block::getLocation)
                .collect(Collectors.toCollection(ArrayList::new)))
            for (Entity entity : player.getNearbyEntities(3.5, 3.5, 3.5))
                if (entity instanceof LivingEntity && isLooking((LivingEntity) entity, location))
                    return (LivingEntity) entity;

        return null;
    }

    public static boolean isLooking(LivingEntity player, Location location)
    {
        BlockIterator it = new BlockIterator(player, 4);

        while (it.hasNext())
        {
            final Block block = it.next();
            if (block.getX() == location.getBlockX() &&
                    block.getY() == location.getBlockY() &&
                    block.getZ() == location.getBlockZ())
                return true;
        }
        return false;
    }

    public static boolean hasPermission(CommandSender perm, String permission)
    {
        if (perm.hasPermission(permission))
            return true;
        perm.sendMessage(ChatColor.RED + "エラー：権限がありません！");
        return false;
    }

    public static boolean isPlayer(CommandSender sender)
    {
        if (sender instanceof Player)
            return true;
        sender.sendMessage(ChatColor.RED + "エラー：プレイヤーから実行する必要があります。");
        return false;
    }

    public static Player getPlayerAllowOffline(String playerName)
    {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers())
                if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(playerName))
                    player = offlinePlayer.getPlayer();

        return player;
    }

    public static boolean invalidLengthMessage(CommandSender sender, String[] args, int min, int max)
    {
        if (args.length < min || args.length > max)
        {
            sender.sendMessage(ChatColor.RED + "エラー：引数の長さがおかしいです！");
            return false;
        }

        return true;
    }
}
