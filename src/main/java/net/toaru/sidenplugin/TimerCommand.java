package net.toaru.sidenplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimerCommand implements CommandExecutor {

    private final TimerManager timerManager;

    public TimerCommand(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはプレイヤーのみ実行可能です。");
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            timerManager.openTimerGUI(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "start":
                if (args.length < 2) {
                    player.sendMessage("使用方法: /timer start <時間:分:秒> [タイマー名]");
                    return true;
                }
                try {
                    int seconds = parseTime(args[1]);
                    String timerName = args.length >= 3 ? args[2] : "default";
                    timerManager.startTimer(player, seconds, timerName);
                } catch (NumberFormatException e) {
                    player.sendMessage("時間は正しいフォーマットで指定してください (例: 1:30:45)。");
                }
                break;
            case "stop":
                if (args.length < 2) {
                    player.sendMessage("使用方法: /timer stop <タイマー名>");
                    return true;
                }
                timerManager.stopTimer(player, args[1]);
                break;
            case "reset":
                if (args.length < 2) {
                    player.sendMessage("使用方法: /timer reset <タイマー名>");
                    return true;
                }
                timerManager.resetTimer(player, args[1]);
                break;
            case "time":
                if (args.length < 2) {
                    player.sendMessage("使用方法: /timer time <タイマー名>");
                    return true;
                }
                timerManager.checkTime(player, args[1]);
                break;
            case "list":
                timerManager.listTimers(player);
                break;
            default:
                sendHelpMessage(player);
                break;
        }

        return true;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage("使用方法:");
        player.sendMessage("/timer start <時間:分:秒> [タイマー名] - タイマーを開始します");
        player.sendMessage("/timer stop <タイマー名> - タイマーを停止します");
        player.sendMessage("/timer reset <タイマー名> - タイマーをリセットします");
        player.sendMessage("/timer time <タイマー名> - タイマーの残り時間を表示します");
        player.sendMessage("/timer list - 動作中のタイマーを表示します");
    }

    private int parseTime(String time) throws NumberFormatException {
        String[] units = time.split(":");
        int hours = 0, minutes = 0, seconds = 0;

        if (units.length == 3) {
            hours = Integer.parseInt(units[0]);
            minutes = Integer.parseInt(units[1]);
            seconds = Integer.parseInt(units[2]);
        } else if (units.length == 2) {
            minutes = Integer.parseInt(units[0]);
            seconds = Integer.parseInt(units[1]);
        } else if (units.length == 1) {
            seconds = Integer.parseInt(units[0]);
        } else {
            throw new NumberFormatException("Invalid time format");
        }

        return hours * 3600 + minutes * 60 + seconds;
    }
}
