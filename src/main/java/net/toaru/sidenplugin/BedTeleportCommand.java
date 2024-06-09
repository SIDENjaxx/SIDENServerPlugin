package net.toaru.sidenplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedTeleportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.getBedSpawnLocation() != null) {
                player.teleport(player.getBedSpawnLocation());
                player.sendMessage("ベッドにテレポートしました！");
            } else {
                player.sendMessage("ベッドが見つかりませんでした。");
            }
            return true;
        } else {
            sender.sendMessage("このコマンドはプレイヤーからのみ使用できます。");
            return false;
        }
    }
}
