package net.toaru.sidenplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimerTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("start", "stop", "reset", "time", "list");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("time"))) {
            return Arrays.asList("default"); // タイマー名の補完 (ここではデフォルトタイマーのみを例示)
        }
        return new ArrayList<>();
    }
}
