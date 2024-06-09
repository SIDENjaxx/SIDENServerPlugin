package net.toaru.sidenplugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SetHomeCommand implements CommandExecutor {
    private final Map<String, Map<String, Location>> homes = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            World world = player.getWorld();
            String playerName = player.getName();

            // ワールドごとのホームを取得または初期化
            Map<String, Location> worldHomes = homes.computeIfAbsent(world.getName(), k -> new HashMap<>());

            // プレイヤーごとのホームを設定
            worldHomes.put(playerName, player.getLocation());
            player.sendMessage("ホームを設定しました");
            return true;
        }
        return false;
    }

    public Location getHome(String playerName, World world) {
        Map<String, Location> worldHomes = homes.get(world.getName());
        if (worldHomes != null) {
            return worldHomes.get(playerName);
        }
        return null;
    }
}
