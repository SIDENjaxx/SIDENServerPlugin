package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class CommandBlockViewer implements Listener {

    private final Main plugin;

    public CommandBlockViewer(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        // プレイヤーがスニーク状態のときに実行
        if (player.isSneaking()) {
            // プレイヤーがスニークしているかつターゲットブロックがコマンドブロックの場合
            Block targetBlock = player.getTargetBlock(null, 5);
            if (targetBlock != null) {
                Material type = targetBlock.getType();
                if (type == Material.COMMAND_BLOCK || type == Material.CHAIN_COMMAND_BLOCK || type == Material.REPEATING_COMMAND_BLOCK) {
                    Bukkit.getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            CommandBlock commandBlock = (CommandBlock) targetBlock.getState();
                            org.bukkit.block.data.type.CommandBlock commandBlockData = (org.bukkit.block.data.type.CommandBlock) commandBlock.getBlockData();

                            // コマンドブロックの種類を取得
                            String typeName = "";
                            switch (type) {
                                case COMMAND_BLOCK:
                                    typeName = "衝撃"; // Impulse
                                    break;
                                case CHAIN_COMMAND_BLOCK:
                                    typeName = "チェーン"; // Chain
                                    break;
                                case REPEATING_COMMAND_BLOCK:
                                    typeName = "リピート"; // Repeat
                                    break;
                            }

                            // サブタイトルにコマンドブロックの種類と条件付きかどうかを表示
                            String conditional = commandBlockData.isConditional() ? ChatColor.GOLD + "はい" : ChatColor.GOLD + "いいえ";
                            player.sendTitle(ChatColor.YELLOW + "種類: " + ChatColor.GOLD + typeName,
                                    ChatColor.YELLOW + "条件付き: " + conditional, 10, 70, 20);

                            // コマンドが登録されているか確認し、登録されていなければアクションバーにメッセージを送信
                            String command = commandBlock.getCommand();
                            if (command != null && !command.isEmpty()) {
                                // アクションバーに登録されているコマンドを表示
                                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                                        new net.md_5.bungee.api.chat.TextComponent(ChatColor.GOLD + "コマンド: " + ChatColor.YELLOW + command));
                            } else {
                                // アクションバーにコマンドが登録されていない場合のメッセージを送信
                                player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                                        new net.md_5.bungee.api.chat.TextComponent(ChatColor.RED + "コマンドが登録されていません"));
                            }
                        }
                    });
                }
            }
        }
    }
}
