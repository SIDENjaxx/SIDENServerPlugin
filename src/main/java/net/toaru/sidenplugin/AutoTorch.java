package net.toaru.sidenplugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class AutoTorch implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Block block = player.getLocation().getBlock();

        // プレイヤーが地面にいるかどうかを確認します
        if (player.isOnGround()) {
            // プレイヤーが松明をオフハンドに持っているかどうかを確認します
            if (player.getInventory().getItemInOffHand().getType() == Material.TORCH) {
                // ブロックの光レベルが一定値以下であるかどうかを確認します
                if (block.getLightLevel() < 7) {
                    // 松明を設置します
                    block.setType(Material.TORCH);
                    // オフハンドから松明を1つ減らします
                    ItemStack torches = player.getInventory().getItemInOffHand();
                    torches.setAmount(torches.getAmount() - 1);
                    player.getInventory().setItemInOffHand(torches);
                }
            }
        }
    }
}
