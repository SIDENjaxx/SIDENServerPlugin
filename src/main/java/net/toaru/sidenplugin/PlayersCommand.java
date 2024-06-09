package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayersCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StringBuilder playerList = new StringBuilder("オンラインプレイヤー: ");
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerList.append(player.getName()).append(", ");
        }
        sender.sendMessage(playerList.toString());
        return true;
    }
}
