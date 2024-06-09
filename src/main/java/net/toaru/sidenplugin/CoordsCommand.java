package net.toaru.sidenplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoordsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage("現在の座標: X=" + player.getLocation().getBlockX() +
                    ", Y=" + player.getLocation().getBlockY() +
                    ", Z=" + player.getLocation().getBlockZ());
            return true;
        }
        return false;
    }
}
