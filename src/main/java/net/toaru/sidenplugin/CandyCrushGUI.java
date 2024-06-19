package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        handleInitialMatches(inventory);  // 初期状態でのマッチを処理
        addClockAndGlass(inventory);

        player.openInventory(inventory);
        score = 0; // スコアをリセット
        timeLimit = 120; // 2分の制限時間

        // タイマーの開始
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLimit <= 0) {
                    player.closeInventory();
                    String endMessage = player.getName() + "は" + score + "点で勝利した";
                    if (score > highScore) {
                        highScore = score;
                        endMessage += " (新記録！)";
                    }
                    player.sendMessage(endMessage);
                    cancel();
                } else {
                    timeLimit--;
                    updateClock(inventory);
                    player.getOpenInventory().setTitle(getInventoryTitle());
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

            if (isValidMove(firstClickSlot, secondClickSlot) && isValidMatch(inventory, firstClickSlot, secondClickSlot)) {
                swapCandies(inventory, firstClickSlot, secondClickSlot);
                firstClickSlot = null;

                // 交換後のキャンディをチェックし、マッチがある場合は削除
                handleMatches(inventory);
            } else {
                firstClickSlot = null;
            }
        }
    }

    private void fillInventoryWithCandies(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % width != width - 1) { // 右列を除外
                ItemStack candy = CandyType.getRandomCandy().toItemStack();
                inventory.setItem(i, candy);
            }
        }
    }

    private void handleInitialMatches(Inventory inventory) {
        handleMatches(inventory);
    }

    public int getScore() {
        return score;
    }

    private void addClockAndGlass(Inventory inventory) {
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta meta = clock.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("制限時間: " + (timeLimit / 60) + ":" + String.format("%02d", timeLimit % 60));
            clock.setItemMeta(meta);
        }
        inventory.setItem(width - 1, clock);

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

    private boolean isValidMatch(Inventory inventory, int slot1, int slot2) {
        swapCandies(inventory, slot1, slot2);
        boolean isValid = !getMatchedSlots(inventory).isEmpty();
        swapCandies(inventory, slot1, slot2); // 元に戻す
        return isValid;
    }

    private void swapCandies(Inventory inventory, int slot1, int slot2) {
        ItemStack item1 = inventory.getItem(slot1);
        ItemStack item2 = inventory.getItem(slot2);
        inventory.setItem(slot1, item2);
        inventory.setItem(slot2, item1);
    }

    private void handleMatches(Inventory inventory) {
        List<Integer> matchedSlots = getMatchedSlots(inventory);
        while (!matchedSlots.isEmpty()) {
            for (int matchedSlot : matchedSlots) {
                inventory.setItem(matchedSlot, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            }
            try {
                Thread.sleep(100); // 一瞬だけ灰色ガラスを表示するための遅延
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int matchedSlot : matchedSlots) {
                inventory.setItem(matchedSlot, new ItemStack(Material.AIR));
            }
            updateScore(matchedSlots.size());
            dropCandies(inventory);
            fillEmptySlots(inventory);
            matchedSlots = getMatchedSlots(inventory);
        }
    }

    private List<Integer> getMatchedSlots(Inventory inventory) {
        List<Integer> matchedSlots = new ArrayList<>();

        for (int row = 0; row < size / width; row++) {
            for (int col = 0; col < width - 2; col++) {
                int slot = row * width + col;
                if (slot % width != width - 1 && isMatching(inventory, slot, slot + 1, slot + 2)) {
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

        return item1.getType() == item2.getType() && item2.getType() == item3.getType();
    }

    private void updateScore(int matchedCount) {
        score += matchedCount * 10;
        plugin.getLogger().info("Score: " + score);
    }

    private void dropCandies(Inventory inventory) {
        for (int col = 0; col < width - 1; col++) {
            for (int row = size / width - 1; row >= 0; row--) {
                int slot = row * width + col;
                if (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {
                    for (int rowAbove = row - 1; rowAbove >= 0; rowAbove--) {
                        int slotAbove = rowAbove * width + col;
                        ItemStack itemAbove = inventory.getItem(slotAbove);
                        if (itemAbove != null && itemAbove.getType() != Material.AIR) {
                            inventory.setItem(slot, itemAbove);
                            inventory.setItem(slotAbove, new ItemStack(Material.AIR));
                            break;
                        }
                    }
                }
            }
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
}
