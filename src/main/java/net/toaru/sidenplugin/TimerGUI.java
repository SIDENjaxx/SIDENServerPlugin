package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TimerGUI implements Listener {

    private final TimerManager timerManager;
    private final Inventory inventory;

    public TimerGUI(TimerManager timerManager) {
        this.timerManager = timerManager;
        this.inventory = Bukkit.createInventory(null, 27, "タイマー管理");
        initializeItems();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    private void initializeItems() {
        inventory.setItem(11, createGuiItem(Material.CLOCK, "タイマー開始", "タイマーを開始します"));
        inventory.setItem(13, createGuiItem(Material.REDSTONE, "タイマー停止", "タイマーを停止します"));
        inventory.setItem(15, createGuiItem(Material.FEATHER, "タイマーリセット", "タイマーをリセットします"));
    }

    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
