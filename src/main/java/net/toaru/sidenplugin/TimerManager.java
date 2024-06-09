package net.toaru.sidenplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class TimerManager {

    private final Map<Player, Map<String, TimerTask>> playerTimers = new HashMap<>();
    private final Main plugin;

    public TimerManager(Main plugin) {
        this.plugin = plugin;
    }

    public void startTimer(Player player, int seconds, String timerName) {
        Map<String, TimerTask> timers = playerTimers.computeIfAbsent(player, k -> new HashMap<>());

        if (timers.containsKey(timerName)) {
            player.sendMessage(Component.text("既にタイマーが動作中です: " + timerName).color(NamedTextColor.RED));
            return;
        }

        TimerTask task = new TimerTask(player, seconds, timerName);
        task.runTaskTimer(plugin, 0L, 20L);
        timers.put(timerName, task);
        player.sendMessage(Component.text("タイマーを " + formatTime(seconds) + " で開始しました: " + timerName).color(NamedTextColor.GREEN));
    }

    public void stopTimer(Player player, String timerName) {
        Map<String, TimerTask> timers = playerTimers.get(player);
        if (timers != null && timers.containsKey(timerName)) {
            TimerTask task = timers.remove(timerName);
            task.cancel();
            player.sendMessage(Component.text("タイマーを停止しました: " + timerName).color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("指定されたタイマーは動作していません: " + timerName).color(NamedTextColor.RED));
        }
    }

    public void resetTimer(Player player, String timerName) {
        stopTimer(player, timerName);
        player.sendMessage(Component.text("タイマーをリセットしました: " + timerName).color(NamedTextColor.GREEN));
    }

    public void checkTime(Player player, String timerName) {
        Map<String, TimerTask> timers = playerTimers.get(player);
        if (timers != null && timers.containsKey(timerName)) {
            TimerTask task = timers.get(timerName);
            player.sendMessage(Component.text("残り時間: " + formatTime(task.getTimeLeft()) + " (" + timerName + ")").color(NamedTextColor.YELLOW));
        } else {
            player.sendMessage(Component.text("指定されたタイマーは動作していません: " + timerName).color(NamedTextColor.RED));
        }
    }

    public void listTimers(Player player) {
        Map<String, TimerTask> timers = playerTimers.get(player);
        if (timers != null && !timers.isEmpty()) {
            player.sendMessage(Component.text("動作中のタイマー:").color(NamedTextColor.GOLD));
            timers.forEach((name, task) -> player.sendMessage(Component.text(name + " - 残り " + formatTime(task.getTimeLeft())).color(NamedTextColor.YELLOW)));
        } else {
            player.sendMessage(Component.text("現在動作中のタイマーはありません。").color(NamedTextColor.GOLD));
        }
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void openTimerGUI(Player player) {
        TimerGUI gui = new TimerGUI(this);
        gui.open(player);
    }

    private class TimerTask extends BukkitRunnable {

        private final Player player;
        private final String timerName;
        private int timeLeft;

        public TimerTask(Player player, int timeLeft, String timerName) {
            this.player = player;
            this.timeLeft = timeLeft;
            this.timerName = timerName;
        }

        @Override
        public void run() {
            if (timeLeft > 0) {
                player.sendActionBar(Component.text("残り時間: " + formatTime(timeLeft) + " (" + timerName + ")").color(NamedTextColor.YELLOW));
                if (timeLeft % 60 == 0) { // 1分毎に通知
                    player.sendMessage(Component.text("タイマー " + timerName + ": 残り " + formatTime(timeLeft)).color(NamedTextColor.YELLOW));
                }
                timeLeft--;
            } else {
                player.sendActionBar(Component.text("タイマーが終了しました: " + timerName).color(NamedTextColor.RED));
                player.sendMessage(Component.text("タイマー " + timerName + " が終了しました。").color(NamedTextColor.RED));
                cancel();
            }
        }

        public int getTimeLeft() {
            return timeLeft;
        }
    }
}
