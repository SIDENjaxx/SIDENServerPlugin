package net.toaru.sidenplugin.utils;

import net.toaru.sidenplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Mention
{
    public static void onMention(Player player, Player sender, String text)
    {
        notificationBossbar(player, sender, text, Main.getPlugin().getConfig().getInt("mention.duration"));
        notificationSound(player);
    }

    public static void notificationSound(Player player)
    {
        playSound(Sound.BLOCK_NOTE_BLOCK_BIT, player, 1.06f);
        playSound(Sound.BLOCK_NOTE_BLOCK_FLUTE, player, 0.8f);
        playSound(Sound.BLOCK_NOTE_BLOCK_FLUTE, player, 0.53f);
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                playSound(Sound.BLOCK_NOTE_BLOCK_BIT, player, 1.42f);
                playSound(Sound.BLOCK_NOTE_BLOCK_FLUTE, player, 1.06f);
                playSound(Sound.BLOCK_NOTE_BLOCK_FLUTE, player, 0.7f);
            }
        }.runTaskLater(Main.getPlugin(), 2L);
    }

    private static void playSound(Sound sound, Player p, float pitch)
    {
        p.playSound(p.getLocation(), sound, SoundCategory.PLAYERS, 1.0f, pitch);
    }

    public static void notificationBossbar(Player player, Player sender, String text, int duration)
    {
        BossBar bar = Bukkit.createBossBar("-", BarColor.BLUE, BarStyle.SOLID);

        bar.setTitle(ChatColor.AQUA + sender.getName() + " - " + ChatColor.RESET + text);
        bar.setVisible(true);
        bar.setProgress(0.0);
        bar.addPlayer(player);

        new BukkitRunnable()
        {
            private final long time = duration * 20L;
            private long now = 0L;

            @Override
            public void run()
            {
                if (now++ < time)
                    bar.setProgress((double) now / (double) time);
                else
                {
                    bar.setVisible(false);
                    bar.removeAll();
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
