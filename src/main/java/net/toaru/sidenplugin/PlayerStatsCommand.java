package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Statistic;
import org.bukkit.Material;

import java.text.SimpleDateFormat;

public class PlayerStatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;
        if (args.length > 0) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("§cそのプレイヤーは見つかりませんでした。");
                return true;
            }
        } else if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage("§cプレイヤー名を指定してください。");
            return true;
        }

        // プレイヤーの統計情報を取得
        int mobsKilled = player.getStatistic(Statistic.MOB_KILLS);
        int distanceWalked = player.getStatistic(Statistic.WALK_ONE_CM);
        int timesJumped = player.getStatistic(Statistic.JUMP);
        int damageTaken = player.getStatistic(Statistic.DAMAGE_TAKEN);
        int xpGained = player.getStatistic(Statistic.ANIMALS_BRED);
        int playTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long lastPlayed = player.getLastPlayed();

        int totalBlocksMined = 0;
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                totalBlocksMined += player.getStatistic(Statistic.MINE_BLOCK, material);
            }
        }

        // 統計情報をプレイヤーに表示
        sender.sendMessage("§6§l--- " + player.getName() + " の統計情報 ---");
        sender.sendMessage("§a掘った全ブロックの数: §f" + totalBlocksMined);
        sender.sendMessage("§a倒したモブの数: §f" + mobsKilled);
        sender.sendMessage("§a歩いた距離(cm): §f" + distanceWalked);
        sender.sendMessage("§aジャンプした回数: §f" + timesJumped);
        sender.sendMessage("§a受けたダメージ: §f" + damageTaken);
        sender.sendMessage("§a獲得した経験値の数: §f" + xpGained);

// サーバーに参加した合計時間を時間と分と秒に変換
        long totalMinutes = playTime / 1200; // 1200 ticks in a minute
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        long seconds = (playTime % 1200) / 20; // 20 ticks in a second

        sender.sendMessage("§aサーバーに参加した合計時間: §f" + hours + "時間 " + minutes + "分 " + seconds + "秒");


        // 最後にサーバーに参加した日時を年と曜日と日にちと時間と分と秒に変換
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年 MM月 dd日 E HH:mm:ss");
        sender.sendMessage("§a最後にサーバーに参加した日時: §f" + sdf.format(new java.util.Date(lastPlayed)));

        sender.sendMessage("§6§l------------------------");

        return true;
    }
}
