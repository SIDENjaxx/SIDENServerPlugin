package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MidnightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.getWorld().setTime(18000);
            player.sendMessage("時間を深夜に変更しました");
        } else {
            sender.sendMessage("このコマンドはプレイヤーからのみ実行できます");
        }
        return true;
    }
}
