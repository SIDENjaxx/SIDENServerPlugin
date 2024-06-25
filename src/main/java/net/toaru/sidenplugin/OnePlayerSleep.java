package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class OnePlayerSleep implements Listener {

    private Main plugin;

    public OnePlayerSleep(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    if (event.getPlayer().isSleeping()) {
                        World world = event.getPlayer().getWorld();
                        world.setTime(1000);
                        for (Player player : world.getPlayers()) {
                            player.sendMessage(ChatColor.GREEN + event.getPlayer().getName() + "が寝ました。時間を昼に変更します。");
                        }
                    }
                }
            }, 100L); // 100 ticks (5 seconds) delay to check if the player is still sleeping
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        World world = event.getPlayer().getWorld();
        for (Player player : world.getPlayers()) {
            player.sendMessage(ChatColor.YELLOW + event.getPlayer().getName() + "がベッドから出ました。");
        }
    }
}
