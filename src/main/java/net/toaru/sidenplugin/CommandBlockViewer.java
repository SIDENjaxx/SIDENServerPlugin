package net.toaru.sidenplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.entity.Player;
import org.bukkit.block.data.type.CommandBlock; // 修正されたインポート

public class CommandBlockViewer implements Listener {

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            Block targetBlock = player.getTargetBlockExact(5);
            if (targetBlock != null && targetBlock.getType() == Material.COMMAND_BLOCK) {
                CommandBlock commandBlock = (CommandBlock) targetBlock.getState();
                String command = commandBlock.getCommand();
                BlockData data = targetBlock.getBlockData();
                org.bukkit.block.data.type.CommandBlock commandBlockData = (org.bukkit.block.data.type.CommandBlock) data;
                boolean isConditional = commandBlockData.isConditional();
                String conditional = isConditional ? "条件付き" : "無条件";

                // コマンドブロックの種類を判別
                org.bukkit.block.data.type.CommandBlock.Type type = commandBlockData.getType(); // 完全修飾名を使用
                String blockType;
                switch (type) {
                    case SEQUENCE:
                        blockType = "チェーン";
                        break;
                    case AUTO:
                        blockType = "リピート";
                        break;
                    case REDSTONE:
                    default:
                        blockType = "衝撃";
                        break;
                }

                // アクションバーにコマンドを表示
                player.sendActionBar(ChatColor.YELLOW + "コマンド: " + command);

                // サブタイトルに種類と条件を表示
                player.sendTitle("", ChatColor.GREEN + "種類: " + blockType + " | " + conditional, 10, 70, 20);
            }
        }
    }
}
