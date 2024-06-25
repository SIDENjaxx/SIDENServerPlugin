package net.toaru.sidenplugin;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class LightLevelCheckCommand implements CommandExecutor {

    private final Set<Player> activePlayers = new HashSet<>();
    private final JavaPlugin plugin;

    public LightLevelCheckCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (activePlayers.contains(player)) {
                activePlayers.remove(player);
                player.sendMessage(ChatColor.GREEN + "光レベルチェックを無効にしました！");
            } else {
                activePlayers.add(player);
                player.sendMessage(ChatColor.GREEN + "光レベルチェックを有効にしました！");

                new BukkitRunnable() {
                    Location lastCheckedLocation = player.getLocation().getBlock().getLocation();

                    @Override
                    public void run() {
                        if (!activePlayers.contains(player)) {
                            this.cancel();
                            return;
                        }

                        Location currentLocation = player.getLocation().getBlock().getLocation();
                        if (!currentLocation.equals(lastCheckedLocation)) {
                            lastCheckedLocation = currentLocation;
                            showParticles(player, currentLocation);
                        }
                    }

                    private void showParticles(Player player, Location center) {
                        for (int x = -10; x <= 10; x++) {
                            for (int z = -10; z <= 10; z++) {
                                Location blockLocation = center.clone().add(x, 0, z);
                                if (blockLocation.getBlock().getType() == Material.AIR) {
                                    int lightLevel = blockLocation.getBlock().getLightLevel();
                                    Particle.DustOptions dustOptions = new Particle.DustOptions(getColorFromLightLevel(lightLevel), 1.0f);
                                    player.getWorld().spawnParticle(Particle.REDSTONE, blockLocation.add(0.5, 1.0, 0.5), 1, dustOptions);
                                }
                            }
                        }
                    }

                    private Color getColorFromLightLevel(int lightLevel) {
                        switch (lightLevel) {
                            case 0: return Color.fromRGB(255, 0, 0); // 赤
                            case 1: return Color.fromRGB(255, 85, 0); // オレンジ
                            case 2: return Color.fromRGB(255, 170, 0); // 黄色
                            case 3: return Color.fromRGB(255, 255, 0); // 明るい黄色
                            case 4: return Color.fromRGB(170, 255, 0); // 明るい緑
                            case 5: return Color.fromRGB(85, 255, 0); // 緑
                            case 6: return Color.fromRGB(0, 255, 0); // 濃い緑
                            case 7: return Color.fromRGB(0, 255, 85); // シアン
                            case 8: return Color.fromRGB(0, 255, 170); // 明るい青
                            case 9: return Color.fromRGB(0, 255, 255); // 青
                            case 10: return Color.fromRGB(0, 170, 255); // 濃い青
                            case 11: return Color.fromRGB(0, 85, 255); // インディゴ
                            case 12: return Color.fromRGB(0, 0, 255); // バイオレット
                            case 13: return Color.fromRGB(85, 0, 255); // 紫
                            case 14: return Color.fromRGB(170, 0, 255); // マゼンタ
                            case 15: return Color.fromRGB(255, 0, 255); // ピンク
                            default: return Color.WHITE;
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            }

            return true;
        }

        return false;
    }
}
