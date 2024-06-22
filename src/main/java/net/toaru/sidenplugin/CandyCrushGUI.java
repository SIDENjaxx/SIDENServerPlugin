package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CandyCrushGUI implements Listener {

    public enum CandyType {
        RED(Material.RED_DYE),
        LIGHT_BLUE(Material.LIGHT_BLUE_DYE),
        GREEN(Material.GREEN_DYE),
        YELLOW(Material.YELLOW_DYE),
        PURPLE(Material.PURPLE_DYE),
        ORANGE(Material.ORANGE_DYE);

        private final Material material;

        CandyType(Material material) {
            this.material = material;
        }

        public Material getMaterial() {
            return material;
        }

        public ItemStack toItemStack() {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(this.name() + " キャンディ");
                item.setItemMeta(meta);
            }
            return item;
        }

        public static CandyType getRandomCandy() {
            CandyType[] values = CandyType.values();
            return values[new Random().nextInt(values.length)];
        }
    }

    private final JavaPlugin plugin;
    private final int size = 54;  // 6x9 のチェストサイズ
    private final int width = 9;
    private int score;
    private int highScore = 0;  // 最高記録を保存するための変数
    private int timeLimit;  // 制限時間（秒）
    private Integer firstClickSlot = null;

    public CandyCrushGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, size, getInventoryTitle());
        fillInventoryWithCandies(inventory);
        addClockAndGlass(inventory);

        player.openInventory(inventory);
        score = 0; // スコアをリセット
        timeLimit = 120; // 2分の制限時間

        // タイマーの開始
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLimit <= 0) {
                    endGame(player);
                    cancel();
                } else {
                    timeLimit--;
                    updateClock(inventory);
                    handleMatches(inventory); // 定期的にマッチチェック
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private String getInventoryTitle() {
        return "キャンディークラッシュ - スコア: " + score;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("キャンディークラッシュ")) return;

        event.setCancelled(true);  // インベントリ内のアイテム移動をキャンセル

        Inventory inventory = event.getInventory();
        int slot = event.getRawSlot();

        if (slot < 0 || slot >= size || slot % width == width - 1) return;  // インベントリ外のクリックと右列を無視

        if (firstClickSlot == null) {
            firstClickSlot = slot;
        } else {
            int secondClickSlot = slot;

            if (isValidMove(firstClickSlot, secondClickSlot)) {
                swapCandies(inventory, firstClickSlot, secondClickSlot);
                event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f);

                if (!handleMatches(inventory)) {
                    swapCandies(inventory, firstClickSlot, secondClickSlot); // マッチがなければ元に戻す
                    event.getWhoClicked().getWorld().playSound(event.getWhoClicked().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                }
                firstClickSlot = null;
            } else {
                firstClickSlot = null;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().startsWith("キャンディークラッシュ")) {
            endGame((Player) event.getPlayer());
        }
    }

    private void endGame(Player player) {
        player.closeInventory();
        String endMessage = player.getName() + "は" + score + "点で勝利した";
        if (score > highScore) {
            highScore = score;
            endMessage += " (新記録！)";
        }
        player.sendMessage(endMessage);
    }

    private void fillInventoryWithCandies(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % width != width - 1) { // 右列を除外
                ItemStack candy = CandyType.getRandomCandy().toItemStack();
                inventory.setItem(i, candy);
            }
        }
    }

    private void addClockAndGlass(Inventory inventory) {
        updateClock(inventory);

        ItemStack grayGlassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = width - 1 + width; i < size; i += width) {
            inventory.setItem(i, grayGlassPane);
        }
    }

    private void updateClock(Inventory inventory) {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("制限時間: " + (timeLimit / 60) + ":" + String.format("%02d", timeLimit % 60));
            clock.setItemMeta(meta);
        }
        inventory.setItem(width - 1, clock);
    }

    private boolean isValidMove(int slot1, int slot2) {
        int row1 = slot1 / width;
        int col1 = slot1 % width;
        int row2 = slot2 / width;
        int col2 = slot2 % width;

        // 1マスの上下左右のみ有効
        return (Math.abs(row1 - row2) == 1 && col1 == col2) || (Math.abs(col1 - col2) == 1 && row1 == row2);
    }

    private void swapCandies(Inventory inventory, int slot1, int slot2) {
        ItemStack item1 = inventory.getItem(slot1);
        ItemStack item2 = inventory.getItem(slot2);
        inventory.setItem(slot1, item2);
        inventory.setItem(slot2, item1);
    }

    private boolean handleMatches(Inventory inventory) {
        List<Integer> matchedSlots = getMatchedSlots(inventory);
        boolean foundMatch = !matchedSlots.isEmpty();

        while (!matchedSlots.isEmpty()) {
            for (int matchedSlot : matchedSlots) {
                inventory.setItem(matchedSlot, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
            updateScore(matchedSlots.size());
            dropCandies(inventory, matchedSlots);
            fillEmptySlots(inventory);
            matchedSlots = getMatchedSlots(inventory);
        }

        return foundMatch;
    }

    private List<Integer> getMatchedSlots(Inventory inventory) {
        List<Integer> matchedSlots = new ArrayList<>();

        for (int row = 0; row < size / width; row++) {
            for (int col = 0; col < width - 2; col++) {
                int slot = row * width + col;
                if (isMatching(inventory, slot, slot + 1, slot + 2)) {
                    matchedSlots.add(slot);
                    matchedSlots.add(slot + 1);
                    matchedSlots.add(slot + 2);
                }
            }
        }

        for (int col = 0; col < width - 1; col++) {
            for (int row = 0; row < size / width - 2; row++) {
                int slot = row * width + col;
                if (isMatching(inventory, slot, slot + width, slot + 2 * width)) {
                    matchedSlots.add(slot);
                    matchedSlots.add(slot + width);
                    matchedSlots.add(slot + 2 * width);
                }
            }
        }

        return matchedSlots;
    }

    private boolean isMatching(Inventory inventory, int slot1, int slot2, int slot3) {
        ItemStack item1 = inventory.getItem(slot1);
        ItemStack item2 = inventory.getItem(slot2);
        ItemStack item3 = inventory.getItem(slot3);

        if (item1 == null || item2 == null || item3 == null) return false;
        if (item1.getType() == Material.AIR || item2.getType() == Material.AIR || item3.getType() == Material.AIR) return false;

        return item1.isSimilar(item2) && item2.isSimilar(item3);
    }

    private void updateScore(int matchedCount) {
        score += matchedCount * 10;
        updateInventoryTitle();
    }

    private void dropCandies(Inventory inventory, List<Integer> matchedSlots) {
        for (int slot : matchedSlots) {
            int col = slot % width;
            for (int row = slot / width; row > 0; row--) {
                int currentSlot = row * width + col;
                int aboveSlot = (row - 1) * width + col;
                inventory.setItem(currentSlot, inventory.getItem(aboveSlot));
            }
            inventory.setItem(col, new ItemStack(Material.AIR)); // 最上段には新しいキャンディが入るためクリア
        }
    }

    private void fillEmptySlots(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % width != width - 1 && (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)) {
                ItemStack candy = CandyType.getRandomCandy().toItemStack();
                inventory.setItem(i, candy);
            }
        }
    }

    public int getScore() {
        // スコアを計算または取得するロジック
        int score = 0;
        // スコアを返す
        return score;
    }

    private void updateInventoryTitle() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTitle().startsWith("キャンディークラッシュ")) {
                Inventory inventory = player.getOpenInventory().getTopInventory();
                player.closeInventory();
                inventory = Bukkit.createInventory(null, size, getInventoryTitle());
                player.openInventory(inventory);
            }
        }
    }
}
