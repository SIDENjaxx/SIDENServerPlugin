package net.toaru.sidenplugin;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerCommand implements Listener, TabExecutor, TabCompleter {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /server <restart|plugin> [plugin name]");
            return false;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            if (args.length == 2) {
                String playerName = args[1];
                Bukkit.getBanList(BanList.Type.NAME).addBan(playerName, "Banned by admin", null, null);
                sender.sendMessage("Player " + playerName + " has been banned.");
                return true;
            } else {
                sender.sendMessage("Usage: /server ban <player name>");
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("kick")) {
            if (args.length == 2) {
                String playerName = args[1];
                Bukkit.getPlayer(playerName).kickPlayer("Kicked by admin");
                sender.sendMessage("Player " + playerName + " has been kicked.");
                return true;
            } else {
                sender.sendMessage("Usage: /server kick <player name>");
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("announce")) {
            if (args.length >= 2) {
                String announcement = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                Bukkit.broadcastMessage(announcement);
                return true;
            } else {
                sender.sendMessage("Usage: /server announce <message>");
                return false;
            }
        }

        if (args[0].equalsIgnoreCase("shutdown")) {
            sender.sendMessage("サーバーをシャットダウンします...");
            shutdownServer();
            return true;
        }

        if (args[0].equalsIgnoreCase("restart")) {
            sender.sendMessage("Server is restarting...");
            restartServer(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("plugin")) {
            if (args.length == 1) {
                sender.sendMessage("Reloading all plugins...");
                reloadAllPlugins(sender);
                return true;
            } else {
                String pluginName = args[1];
                Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
                if (plugin != null) {
                    sender.sendMessage("Reloading plugin: " + pluginName);
                    reloadPlugin(sender, plugin);
                    return true;
                } else {
                    sender.sendMessage("Plugin not found: " + pluginName);
                    return false;
                }
            }
        }

        sender.sendMessage("Usage: /server <restart|plugin> [plugin name]");
        return false;
    }

    private void shutdownServer() {
        Bukkit.shutdown(); // サーバーをシャットダウン
    }

    private void restartServer(CommandSender sender) {
        try {
            // シェルスクリプトやバッチファイルを実行してサーバーを再起動する
            if (System.getProperty("os.name").startsWith("Windows")) {
                Runtime.getRuntime().exec("cmd /c start restart.bat");
            } else {
                Runtime.getRuntime().exec("/bin/sh restart.sh");
            }
            Bukkit.shutdown(); // サーバーをシャットダウン
        } catch (IOException e) {
            sender.sendMessage("Error: Unable to restart the server.");
            e.printStackTrace();
        }
    }

    private void reloadAllPlugins(CommandSender sender) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            reloadPlugin(sender, plugin);
        }
    }

    private void reloadPlugin(CommandSender sender, Plugin plugin) {
        try {
            Bukkit.getPluginManager().disablePlugin(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
            sender.sendMessage("Successfully reloaded plugin: " + plugin.getName());
        } catch (Exception e) {
            sender.sendMessage("Error: Unable to reload plugin: " + plugin.getName());
            e.printStackTrace();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("restart", "plugin", "shutdown", "ban", "kick", "announce");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("kick")) {
                List<String> playerNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                return playerNames;
            } else if (args[0].equalsIgnoreCase("plugin")) {
                List<String> pluginNames = new ArrayList<>();
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    pluginNames.add(plugin.getName());
                }
                return pluginNames;
            }
        }
        return null;
    }
}