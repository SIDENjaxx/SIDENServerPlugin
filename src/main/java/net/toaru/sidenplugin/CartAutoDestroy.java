package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class CartAutoDestroy implements Listener {
    private JavaPlugin plugin;

    public CartAutoDestroy(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            Minecart minecart = (Minecart) event.getVehicle();
            if (event.getExited() instanceof Player) {
                Player player = (Player) event.getExited();
                ItemStack itemStack = new ItemStack(Material.MINECART, 1);
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    public void run() {
                        minecart.remove();
                    }
                }, 0L); // 20ティックの遅延
            }
        }
    }
}
