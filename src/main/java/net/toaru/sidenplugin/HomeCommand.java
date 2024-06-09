package net.toaru.sidenplugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final SetHomeCommand setHomeCommand;

    public HomeCommand(SetHomeCommand setHomeCommand) {
        this.setHomeCommand = setHomeCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = player.getWorld();
            Location home = setHomeCommand.getHome(player.getName(), world);
            if (home != null) {
                player.teleport(home);
                player.sendMessage("ホームにテレポートしました");
            } else {
                player.sendMessage("ホームが設定されていません");
            }
            return true;
        }
        return false;
    }
}
