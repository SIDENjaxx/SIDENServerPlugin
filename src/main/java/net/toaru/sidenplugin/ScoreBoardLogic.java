package net.toaru.sidenplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ScoreBoardLogic {

    /**
     * Tabを押した際表示されるプレイヤーリストに投票の状態を表示する。
     *
     * @param pattern 数値 0=投票開始時,1=投票時,2=投票終了時
     * @param sender プレイヤー 投票者
     */

    static void setVoteStatus(int pattern, Player sender){
        //投票開始時の処理
        if(pattern==0){
            sender.setPlayerListName(ChatColor.GRAY+" × "+ChatColor.WHITE+sender.getName());
        }
        //投票時
        else if(pattern==1){
            sender.setPlayerListName(ChatColor.GOLD+" ✓ "+ChatColor.WHITE+sender.getName());
        }
        //投票終了時
        else{
            Bukkit.getOnlinePlayers().forEach(player -> {
            player.setPlayerListName(player.getName());
        });
        }
    }

    /**
     * Tabを押した際表示されるプレイヤーリストに投票の状態を表示する。
     *
     * @param sender プレイヤー名 投票者
     * @param receiver プレイヤー名 投票先
     */
    static void setVoteResult(String sender,String receiver){
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(player.getName().equals(sender)) {
                if(Main.YanaGet&&receiver.equals("Yanaaaaa")&&Main.Yvote){
                    player.setPlayerListName(player.getName() + " : " + ChatColor.MAGIC + "????????");
                }else {
                    player.setPlayerListName(player.getName() + " : " + ChatColor.AQUA + receiver);
                }
            }
        });
    }
}
