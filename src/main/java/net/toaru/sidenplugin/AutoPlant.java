package net.toaru.sidenplugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class AutoPlant implements Listener {

    private final JavaPlugin plugin;
    private final Map<Material, Material> cropToSeedMap;

    public AutoPlant(JavaPlugin plugin) {
        this.plugin = plugin;
        this.cropToSeedMap = new HashMap<>();
        this.cropToSeedMap.put(Material.WHEAT, Material.WHEAT_SEEDS);
        this.cropToSeedMap.put(Material.CARROTS, Material.CARROT);
        this.cropToSeedMap.put(Material.POTATOES, Material.POTATO);
        this.cropToSeedMap.put(Material.BEETROOTS, Material.BEETROOT_SEEDS);
        // 他の作物を追加する場合はここに追記します
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Material type = block.getType();

        if (cropToSeedMap.containsKey(type)) {
            ItemStack[] drops = block.getDrops().toArray(new ItemStack[0]);
            Material seedType = cropToSeedMap.get(type);
            if (seedType != null && player.getInventory().containsAtLeast(new ItemStack(seedType), 1)) {
                player.getInventory().removeItem(new ItemStack(seedType, 1));
                block.setType(Material.AIR);
                block.setType(type);
                Ageable newAgeable = (Ageable) block.getBlockData();
                newAgeable.setAge(0);
                block.setBlockData(newAgeable);

                for (ItemStack drop : drops) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Material type = block.getType();

        if (cropToSeedMap.containsValue(type)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (int x = -2; x <= 2; x++) {
                        for (int z = -2; z <= 2; z++) {
                            Block relativeBlock = block.getRelative(x, 0, z);
                            Block belowBlock = relativeBlock.getRelative(BlockFace.DOWN);
                            if (relativeBlock.getType() == Material.AIR && belowBlock.getType() == Material.FARMLAND) {
                                Material seedType = cropToSeedMap.entrySet().stream()
                                        .filter(entry -> entry.getValue().equals(type))
                                        .map(Map.Entry::getKey)
                                        .findFirst()
                                        .orElse(null);
                                if (seedType != null && player.getInventory().containsAtLeast(new ItemStack(seedType), 1)) {
                                    player.getInventory().removeItem(new ItemStack(seedType, 1));
                                    relativeBlock.setType(seedType);
                                    Ageable ageable = (Ageable) relativeBlock.getBlockData();
                                    ageable.setAge(0);
                                    relativeBlock.setBlockData(ageable);
                                }
                            }
                        }
                    }
                }
            }.runTaskLater(plugin, 1L);
        }
    }
}
