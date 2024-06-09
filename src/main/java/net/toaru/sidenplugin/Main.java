package net.toaru.sidenplugin;

import net.toaru.sidenplugin.chat.ChatCommand;
import net.toaru.sidenplugin.commands.CommandHat;
import net.toaru.sidenplugin.commands.HeadCommand;
import net.toaru.sidenplugin.commands.CommandUnHat;
import net.toaru.sidenplugin.events.HatEventListener;
import net.toaru.sidenplugin.events.MentionEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.configuration.Configuration;

import java.util.logging.Logger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Objects;



public final class Main extends JavaPlugin implements CommandExecutor, TabCompleter {

    private static Main plugin;
    public ChatCommand chatCommand;
    public TimerManager getTimerManager() {
        return timerManager;
    }
    public static Main getPlugin() {
        return plugin;
    }
    private TimerManager timerManager;
    private InventoryItemCounter inventoryItemCounter;
    public static Logger log;
    private static Main instance;
    public static int cooldown;
    public static boolean enableSaplings;
    public static boolean enableCrops;
    public static boolean showParticles;
    public static int blockRadius;
    public static int mobRadius;
    public static double blockPercentage;
    public static double mobPercentage;

    private List<String> getFrom(String[] args, int index) {
        if (args.length > index)
            return Arrays.asList(args).subList(index, args.length);
        return Collections.emptyList();
    }

    private Optional<String> checkPermission(CommandSender sender, String name) {
        if (sender.hasPermission("assay.passthrough." + name))
            return Optional.empty();

        if (!sender.hasPermission("assay.offline")) {
            Player player = Bukkit.getPlayer(name);
            if (player == null)
                return Optional.of("オフラインの人として発言する");
        }

        if (!sender.hasPermission("assay.outside")) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (!offlinePlayer.hasPlayedBefore())
                return Optional.of("鯖に入ったことのない人として発言する");
        }

        if (!sender.hasPermission("assay.as." + name))
            return Optional.of("ホワイトリストに追加されていない人として発言する");

        return Optional.empty();
    }

    @Override
    public void onEnable() {
        instance = this;
        // コマンドの登録
        saveDefaultConfig();
        inventoryItemCounter = new InventoryItemCounter(this);
        inventoryItemCounter.start();

        // イベントリスナーの登録
        getServer().getPluginManager().registerEvents(new CommandBlockViewer(this), this);
        this.getCommand("omikuji").setExecutor(new OmikujiCommand());
        getCommand("stats").setExecutor(new PlayerStatsCommand());
        getServer().getPluginManager().registerEvents(inventoryItemCounter, this);
        SetHomeCommand setHomeCommand = new SetHomeCommand();
        getCommand("sethome").setExecutor(setHomeCommand);
        getCommand("home").setExecutor(new HomeCommand(setHomeCommand));
        getCommand("day").setExecutor(new DayCommand());
        getCommand("night").setExecutor(new NightCommand());
        getCommand("coords").setExecutor(new CoordsCommand());
        getCommand("players").setExecutor(new PlayersCommand());
        getCommand("server").setExecutor(new RestartPlugin());
        getCommand("server").setTabCompleter(new RestartPlugin());
        this.timerManager = new TimerManager(this);
        TimerCommand timerCommand = new TimerCommand(this.timerManager);
        getCommand("timer").setExecutor(timerCommand);
        getCommand("timer").setTabCompleter(new TimerTabCompleter());
        Logger logger = this.getLogger();
        getCommand("item").setExecutor(new ItemEdit(logger));
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new AutoTorch(), this);
        Bukkit.getPluginManager().registerEvents(new FarmProtect(), this);
        getServer().getPluginManager().registerEvents(new AutoPlant(this), this);
        saveDefaultConfig();
        saveConfig();
        this.getCommand("v").setExecutor(this);
        this.getCommand("v").setTabCompleter(this);
        this.getCommand("yvote").setExecutor(this);
        this.getCommand("vs").setExecutor(this);
        this.getCommand("vget").setExecutor(this);
        getServer().getLogger().info(ChatColor.AQUA + "NewVotePlugin by Yanaaaaa");
        log = getLogger();

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        Configuration config = getConfig();
        cooldown = config.getInt("Tweaks.cooldownTicks", 5);
        enableSaplings = config.getBoolean("Tweaks.growSaplings", true);
        enableCrops = config.getBoolean("Tweaks.growCrops", true);
        showParticles = config.getBoolean("Tweaks.showParticles", true);
        blockRadius = config.getInt("Tweaks.blockRadius", 5);
        mobRadius = config.getInt("Tweaks.mobRadius", 5);
        blockPercentage = config.getDouble("Tweaks.blockPercentage", 0.1);
        mobPercentage = config.getDouble("Tweaks.mobPercentage", 0.05);
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new SneakHandler(), this);
        plugin = this;
        saveDefaultConfig();
        getCommand("head").setExecutor(new HeadCommand());
        getCommand("head").setTabCompleter(new HeadCommand());
        getCommand("hat").setExecutor(new CommandHat());
        getCommand("unhat").setExecutor(new CommandUnHat());
        Bukkit.getPluginManager().registerEvents(new HatEventListener(), this);
        MentionEventListener mention = new MentionEventListener(this);
        Bukkit.getPluginManager().registerEvents(mention, this);
        chatCommand = new ChatCommand("!", this);
    }


    //vs=投票時判定用,vget=投票開示時判定用
    static boolean vs = false, vget = false, vlist = false;
    //SenderList=投票者のリスト,ReceiverList=被投票者のリスト
    static List<String> SenderList1 = new ArrayList<>(), ReceiverList1 = new ArrayList<>();
    static List<String> SenderList2 = new ArrayList<>(), ReceiverList2 = new ArrayList<>();
    //List=投票先のリスト
    static List<String> List = new ArrayList<>();
    public boolean Yana = false;
    static boolean YanaGet = false, Yvote = true;
    public int Yanum;
    public int rank = 1;
    public String ys;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
        //投票時に関する処理
        if (cmd.getName().equals("v")) {
            if (args.length == 1) {
                //投票時の処理
                if (vs) {
                    //オンラインプレイヤーを取得しリストに登録する処理
                    Player p = (Player) sender;
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        List.add(player.getName());
                    });
                    //もしconfig.ymlのListに要素が登録されていた時にその要素を参照する処理
                    reloadConfig();
                    saveConfig();
                    if (getConfig().getStringList("List").size() != 0) {
                        List = getConfig().getStringList("List");
                    }
                    List.stream().distinct();

                    //投票の受付処理
                    //例外:投票済みの場合の処理
                    if (SenderList1.contains(sender.getName())) {
                        sender.sendMessage(ChatColor.YELLOW + "あなたはすでに投票済みです。");
                    }
                    //例外:投票先のリストにプレイヤーが存在しない場合
                    else if (!List.contains(args[0])) {
                        sender.sendMessage(ChatColor.RED + (args[0] + "はリストに存在しません。"));
                    }
                    //投票完了の通知
                    else {
                        //投票者、投票先を保存し投票完了を通知する。
                        SenderList1.add(p.getName());
                        ReceiverList1.add(args[0]);
                        ScoreBoardLogic.setVoteStatus(1, p);
                        sender.sendMessage(ChatColor.GREEN + args[0] + "に投票しました。");
                    }
                }
                //例外:投票が開始されていないときの処理
                else {
                    sender.sendMessage(ChatColor.RED + "投票は開始されていません。");
                }
            }
            //例外:コマンドの形式が異なっているときの処理。
            else {
                sender.sendMessage(ChatColor.RED + "コマンドの形式が異なります。/v <投票先のプレイヤー名>で投票してください。");
            }
        } else if (cmd.getName().equals("yvote")) {
            if (sender.getName().equals("Yanaaaaa")) {
                if (args.length == 0) {
                    if (Yvote) {
                        Yvote = false;
                        sender.sendMessage(ChatColor.GOLD + "やなーもーどおふ");
                    } else {
                        Yvote = true;
                        sender.sendMessage(ChatColor.GOLD + "やなーもーどおん");
                    }
                } else if (args.length == 1) {
                    rank = Integer.parseInt(args[0]);
                    sender.sendMessage(ChatColor.GOLD + "基準を" + args[0] + "位以上にしました");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "権限がありません");
            }
        }
        //投票開始・投票結果開示に関する処理
        else if (cmd.getName().equals("vs")) {
            if (args.length == 0) {
                if (sender.isOp()) {
                    if (vlist) {
                        ScoreBoardLogic.setVoteStatus(2, (Player) sender);
                        vlist = false;
                        sender.sendMessage(ChatColor.GREEN + "Tabリストから投票先の表示を削除しました。");
                    }
                    //一度目の/vsコマンド使用時に関する処理(投票開始)
                    else if (!vs) {
                        reloadConfig();
                        saveConfig();
                        //投票開始の連絡を全プレイヤーに行う
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage(ChatColor.GOLD + "投票が開始されました。");
                            player.sendMessage(ChatColor.GREEN + "/v <投票先のプレイヤー名>で投票してください。");
                            ScoreBoardLogic.setVoteStatus(0, player);
                        });
                        //投票開示を有効化する。
                        vs = true;
                        vget = false;
                        YanaGet = false;
                        Yana = false;

                    }
                    //二度目の/vsコマンド実行時に関する処理(投票結果開示)
                    else {
                        //数値の引き渡し
                        SenderList2 = SenderList1;
                        SenderList1 = new ArrayList<>();
                        ReceiverList2 = ReceiverList1;
                        ReceiverList1 = new ArrayList<>();
                        //投票結果の表示
                        sendVotingResult(ReceiverList2);
                        ScoreBoardLogic.setVoteStatus(2, (Player) sender);
                        List = new ArrayList<>();
                        //投票開始、投票先開示を有効にする
                    }
                }
                //例外:非OP権限者実行時
                else {
                    sender.sendMessage(ChatColor.RED + "このコマンドを実行する権限がありません。必要権限:OP");
                }
            }
            //例外:コマンドの形式が異なっているときの処理。
            else {
                sender.sendMessage(ChatColor.RED + "コマンドの形式が異なります。/vs で投票を開始することができます。");
            }
        }
        //投票先開示に関する処理
        else if (cmd.getName().equals("vget"))
            if (args.length == 0) {
                if (sender.isOp()) {
                    //投票先開示の処理
                    if (vget) {
                        //各投票者の投票先の表示
                        VoteResultLogic.sendVotingDestination(SenderList2, ReceiverList2);
                        //投票開始を有効、投票先開示を無効にする。
                        vs = false;
                        vget = false;
                        vlist = true;
                    }
                    //例外:投票が開始もしくは終了されていないときの処理
                    else {
                        sender.sendMessage(ChatColor.RED + "投票が完了していません。");
                    }
                }
                //非OP権限者実行時
                else {
                    sender.sendMessage(ChatColor.RED + "このコマンドを実行する権限がありません。必要権限:OP");
                }
            }
            //例外:コマンドの形式が異なっているときの処理。
            else {
                sender.sendMessage(ChatColor.RED + "コマンドの形式が異なります。/vget で投票先を手得できます。");
            }
        if (args.length < 2)
            return false;

        String name = args[0];
        Optional<String> reason = checkPermission(sender, name);
        if (reason.isPresent()) {
            sender.sendMessage(new ComponentBuilder()
                    .append("[かめすたプラグイン] ").color(ChatColor.LIGHT_PURPLE)
                    .append(reason.get() + "ためには権限が足りません").color(ChatColor.RED)
                    .create()
            );
            return true;
        }

        String text = String.join(" ", getFrom(args, 1));
        Bukkit.broadcast(new TranslatableComponent(
                "chat.type.text",
                new TextComponent(new ComponentBuilder()
                        .append(new TextComponent(new ComponentBuilder()
                                .append(name)
                                .append(sender.hasPermission("assay.realname." + name) ? "" : "...?")
                                .create()
                        ))
                        .event(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder()
                                        .append("実は ").color(ChatColor.WHITE)
                                        .append(sender.getName()).color(ChatColor.YELLOW)
                                        .append(" で～す m9(^Д^)").color(ChatColor.WHITE)
                                        .create()
                        ))
                        .create()
                ),
                new TextComponent(text)
        ));
        Bukkit.getPluginManager().callEvent(new AsSayEvent(sender, name, text));

        return true;
    }

    //・/v <投票先>コマンド実行時のTab補完
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String Label, String[] args) {
        if (args.length == 1) {
            if (cmd.getName().equals("v")) {
                reloadConfig();
                saveConfig();
                //Config参照時のTab補完
                if (getConfig().getStringList("List").size() != 0) {
                    return getConfig().getStringList("List");
                }
                //通常時(オンラインプレイヤー参照時)のTab補完
                else {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(s -> s.startsWith(args[0]))
                            .collect(Collectors.toList());
                }
            }
        }
        switch (args.length) {
            case 1:
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(e -> !checkPermission(sender, e).isPresent())
                        .filter(e -> e.startsWith(args[0]))
                        .collect(Collectors.toList());
            case 2:
                return Collections.singletonList("<text>");
        }

        return Collections.emptyList();
    }

    /**
     * 投票結果チャット欄に生成する
     *
     * @param receivers 　リスト　投票先のリスト
     */
    public void sendVotingResult(List<String> receivers) {
        //リストの重複要素数と被投票者をもとにしたMapを生成する
        Map<String, Integer> map = new HashMap<>();
        for (String s : receivers) {
            Integer i = map.get(s);
            map.put(s, i == null ? 1 : i + 1);
        }

        //生成したMapを数字基準で降順にソートする
        List<Map.Entry<String, Integer>> list_entries = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list_entries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                return obj2.getValue().compareTo(obj1.getValue());
            }
        });

        //投票結果の表示処理(全プレイヤーに表示)
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(ChatColor.GOLD + "投票結果を表示します。");
            player.sendMessage(ChatColor.AQUA + "==========投票結果==========");
        });
        int n = 1;//順位用変数
        int count = 1;//実行回数変数
        int num = 1;//数値比較用変数
        for (Map.Entry<String, Integer> entry : list_entries) {
            if (count != 1) {
                if (num != entry.getValue()) {
                    n = n + 1;
                }
            }
            String string = Integer.toString(n);
            int finalN = n;
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage(ChatColor.GREEN + string + "位: " + ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " [" + entry.getValue() + "票]");
                if (entry.getKey().equals("Yanaaaaa") && finalN <= rank) {
                    Yana = true;
                    Yanum = entry.getValue();
                    ys = string;
                }
            });
            num = entry.getValue();
            count++;
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(ChatColor.AQUA + "==========投票結果==========");
        });

        if (Yana && Yvote) {
            Yana = false;
            YanaGet = true;
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendTitle(ChatColor.AQUA + "おや？投票結果の様子が...!?", null, 5, 60, 5);
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5, 1);
            });
            new BukkitRunnable() {
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendTitle(ChatColor.GREEN + "やなぱわ～", null, 5, 40, 5);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
                    });
                    vs = false;
                    vget = true;
                }
            }.runTaskLater(this, 120L);
            new BukkitRunnable() {
                public void run() {
                    if (Yanum >= 0) {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendTitle(ChatColor.GREEN + ys + "位: " + ChatColor.WHITE + " Yanaaaaa" + ChatColor.GOLD + " [" + Yanum + "票]", null, 0, 20, 0);
                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 5, 1);
                        });
                        Yanum--;
                    } else {

                        //投票結果の表示処理(全プレイヤーに表示)
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage(ChatColor.GOLD + "投票結果を再表示します。");
                            player.sendTitle(ChatColor.GOLD + "投票結果を再表示します。", null, 5, 20, 5);
                            player.sendMessage(ChatColor.AQUA + "==========投票結果==========");
                        });
                        int n = 1;//順位用変数
                        int count = 1;//実行回数変数
                        int num = 1;//数値比較用変数
                        for (Map.Entry<String, Integer> entry : list_entries) {
                            if (!entry.getKey().equals("Yanaaaaa")) {
                                if (count != 1) {
                                    if (num != entry.getValue()) {
                                        n = n + 1;
                                    }
                                }
                                String string = Integer.toString(n);
                                Bukkit.getOnlinePlayers().forEach(player -> {
                                    player.sendMessage(ChatColor.GREEN + string + "位: " + ChatColor.WHITE + entry.getKey() + ChatColor.GOLD + " [" + entry.getValue() + "票]");
                                });
                                num = entry.getValue();
                                count++;
                            }
                        }
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage(ChatColor.AQUA + "==========投票結果==========");
                        });
                        this.cancel();
                    }

                }
            }.runTaskTimer(this, 200L, 15L);
        } else {
            vs = false;
            vget = true;
        }

    }
}



