package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TabTPS {

    private JavaPlugin plugin;
    private long updateInterval;

    public TabTPS(JavaPlugin plugin, long updateInterval) {
        this.plugin = plugin;
        this.updateInterval = updateInterval;
    }

    public void scheduleTabUpdates() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double[] tps = Bukkit.getTPS();
                double mspt = Bukkit.getAverageTickTime();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        updateTabList(tps, mspt);
                    }
                }.runTask(plugin);
            }
        }.runTaskTimerAsynchronously(plugin, 0L, updateInterval);
    }

    private void updateTabList(double[] tps, double mspt) {
        String footer = ChatColor.GRAY + "TPS" + ChatColor.WHITE + ": " + ChatColor.GREEN + format(tps[0]) + ChatColor.GRAY + " MSPT" + ChatColor.WHITE + ": " + ChatColor.GREEN + format(mspt) + ChatColor.RESET;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeaderFooter("", footer);
        }
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }
}
