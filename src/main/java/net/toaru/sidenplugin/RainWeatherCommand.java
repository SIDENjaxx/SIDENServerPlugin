package net.toaru.sidenplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RainWeatherCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rain")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.getWorld().setStorm(true);
                player.getWorld().setThundering(false);
                sender.sendMessage("天気を雨にしました。");
                return true;
            } else {
                sender.sendMessage("このコマンドはプレイヤーから実行してください。");
            }
        }
        return false;
    }
}
