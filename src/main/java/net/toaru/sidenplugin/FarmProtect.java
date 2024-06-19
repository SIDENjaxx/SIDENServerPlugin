package net.toaru.sidenplugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;

public class FarmProtect implements Listener {


    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.FARMLAND) {
            event.setCancelled(true); // 耕地の物理更新をキャンセル
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block ->
                block.getType() == Material.FARMLAND ||
                        isCrop(block.getType())
        );
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true); // 畑をジャンプなどで踏んでも荒れないようにする
        }

        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FARMLAND) {
            // プレイヤー自身による破壊を許可
            if (!event.getPlayer().hasPermission("farmprotection.bypass")) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isCrop(Material material) {
        return material == Material.WHEAT ||
                material == Material.CARROTS ||
                material == Material.POTATOES ||
                material == Material.BEETROOTS ||
                material == Material.MELON_STEM ||
                material == Material.PUMPKIN_STEM;
    }
}
