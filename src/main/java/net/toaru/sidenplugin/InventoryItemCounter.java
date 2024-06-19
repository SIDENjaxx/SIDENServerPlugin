package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryItemCounter implements Listener {
    private final Map<UUID, String> playerItemNames = new HashMap<>();
    private final JavaPlugin plugin;
    private final Map<UUID, Integer> playerItemCounts = new HashMap<>();
    private final FileConfiguration config;
    private BukkitRunnable task;

    public InventoryItemCounter(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        try {
                            updateActionBar(player);
                        } catch (Exception e) {
                            plugin.getLogger().warning("プレイヤー " + player.getName() + " のアクションバー更新中にエラーが発生しました: " + e.getMessage());
                        }
                    }
                });
            }
        };
        task.runTaskTimer(plugin, 0, config.getInt("update-interval", 1));
    }

    public void stop() {
        if (task != null) {
            task.cancel();
        }
    }

    private void updateActionBar(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand != null && !itemInMainHand.getType().isAir() && itemInMainHand.getMaxStackSize() > 1) {
            String itemName = getItemName(itemInMainHand);
            int itemCount = countItemsInInventory(player, itemInMainHand);

            UUID playerUUID = player.getUniqueId();
            if (playerItemCounts.containsKey(playerUUID) && playerItemCounts.get(playerUUID) == itemCount
                    && playerItemNames.containsKey(playerUUID) && playerItemNames.get(playerUUID).equals(itemName)) {
                return;
            }

            playerItemCounts.put(playerUUID, itemCount);
            playerItemNames.put(playerUUID, itemName);
            player.sendActionBar(formatActionBarMessage(itemName, itemCount));
        } else {
            clearActionBar(player);
        }
    }

    private void clearActionBar(Player player) {
        player.sendActionBar("");
        playerItemCounts.remove(player.getUniqueId());
        playerItemNames.remove(player.getUniqueId());
    }

    private String formatActionBarMessage(String itemName, int itemCount) {
        return ChatColor.GREEN + "現在のアイテム: " + ChatColor.GOLD + ChatColor.BOLD + itemName + ChatColor.RESET + ChatColor.GREEN + " | 数量: " + ChatColor.YELLOW + ChatColor.BOLD + itemCount + "個";
    }

    private String getItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        } else {
            return getDefaultItemName(item.getType());
        }
    }

    private String getDefaultItemName(Material material) {
        String itemName = material.toString().toLowerCase().replace('_', ' ');
        String[] words = itemName.split(" ");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            formattedName.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return formattedName.toString().trim();
    }

    private int countItemsInInventory(Player player, ItemStack itemInMainHand) {
        int totalItemCount = 0;
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();

        for (ItemStack item : items) {
            if (item != null && item.isSimilar(itemInMainHand)) {
                totalItemCount += item.getAmount();
            }
        }

        return totalItemCount;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> updateActionBar(player), 1L);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        clearActionBar(player);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> updateActionBar(player), 2L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getInventory().getItemInMainHand().getType().isBlock()) {
            clearActionBar(player);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> updateActionBar(player), 1L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clearActionBar(event.getPlayer());
    }
}
