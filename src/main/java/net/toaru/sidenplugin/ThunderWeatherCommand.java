package net.toaru.sidenplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThunderWeatherCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("thunder")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(true);
                sender.sendMessage("天気を雷雨にしました。");
                return true;
            } else {
                sender.sendMessage("このコマンドはプレイヤーから実行してください。");
            }
        }
        return false;
    }
}
