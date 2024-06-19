package net.toaru.sidenplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class OmikujiCommand implements CommandExecutor {

    private Map<UUID, Long> cooldowns = new HashMap<>();
    // おみくじの結果の配列
    private static final String[] RESULTS = {
            "大吉", "中吉", "小吉", "吉", "末吉", "凶", "大凶", "超大吉"
    };

    // コマンドが実行されたときに呼び出されるメソッド
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // ランダムな結果を選択（ThreadLocalRandomはRandomより高性能）
            int index = ThreadLocalRandom.current().nextInt(RESULTS.length);
            String result = RESULTS[index];

            // 結果に応じた色とメッセージを設定
            String message = getResultMessage(result);

            // プレイヤーに結果を表示
            player.sendMessage(message);
            // プレイヤーに音を再生して結果をより印象的に
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            // パーティクルエフェクトを再生
            playResultParticle(player, result);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "このコマンドはプレイヤーのみが使用できます。");
            return true;
        }
    }

    // 結果に応じた色とメッセージを返すメソッド
    private String getResultMessage(String result) {
        ChatColor color = getResultColor(result);
        return color + "あなたのおみくじの結果は... " + result + " です！";
    }

    // 結果に応じた色を返すメソッド
    private ChatColor getResultColor(String result) {
        switch (result) {
            case "大吉":
                return ChatColor.GOLD;
            case "中吉":
                return ChatColor.GREEN;
            case "小吉":
                return ChatColor.AQUA;
            case "吉":
                return ChatColor.YELLOW;
            case "末吉":
                return ChatColor.WHITE;
            case "凶":
                return ChatColor.RED;
            case "大凶":
                return ChatColor.DARK_RED;
            case "超大吉":
                return ChatColor.LIGHT_PURPLE;
            default:
                return ChatColor.GRAY;
        }
    }

    // 結果に応じたパーティクルエフェクトを返すメソッド
    private void playResultParticle(Player player, String result) {
        Location location = player.getLocation();
        Particle particle = getParticleByResult(result);

        // パーティクルエフェクトをプレイヤーの周囲に表示
        player.getWorld().spawnParticle(particle, location, 30, 0.5, 0.5, 0.5, 0.1);
    }

    // 結果に応じたパーティクルを返すメソッド
    private Particle getParticleByResult(String result) {
        switch (result) {
            case "大吉":
                return Particle.FIREWORKS_SPARK;
            case "中吉":
                return Particle.VILLAGER_HAPPY;
            case "小吉":
                return Particle.ENCHANTMENT_TABLE;
            case "吉":
                return Particle.NOTE;
            case "末吉":
                return Particle.END_ROD;
            case "凶":
                return Particle.SMOKE_NORMAL;
            case "大凶":
                return Particle.SMOKE_LARGE;
            case "超大吉":
                return Particle.HEART;
            default:
                // Check if the BARRIER particle exists before returning it
                try {
                    return Particle.valueOf("BARRIER");
                } catch (IllegalArgumentException e) {
                    // Fallback to a default particle if BARRIER does not exist
                    return Particle.NOTE;
                }
        }
    }
}
