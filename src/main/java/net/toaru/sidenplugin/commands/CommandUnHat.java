package net.toaru.sidenplugin.commands;

import net.toaru.sidenplugin.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class CommandUnHat implements CommandExecutor
{
    public static void unHatEntities(Entity entity)
    {
        List<Entity> passengers = entity.getPassengers();

        Location entityLoc = entity.getLocation();

        passengers.forEach(ent -> {
            ent.leaveVehicle();
            ent.teleport(entityLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            unHatEntities(ent);
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!Utils.hasPermission(sender, "seikatsu.unhat") ||
                !Utils.isPlayer(sender))
            return true;

        Player player = (Player) sender;

        if (player.getPassengers().size() != 0)
        {
            unHatEntities(player);
            return true;
        }

        player.sendMessage(ChatColor.RED + "エラー：かぶっているエンティティはありません！");
        return true;
    }

}
