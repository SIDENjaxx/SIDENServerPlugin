package net.toaru.sidenplugin.commands;

import net.toaru.sidenplugin.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CommandHat implements CommandExecutor
{
    public static void hatEntity(Player player, LivingEntity entity)
    {
        if (!player.hasPermission("seikatsu.hat.entity") &&
                !player.hasPermission("seikatsu.hat.player"))
        {
            player.sendMessage(ChatColor.RED + "エラー：権限がありません！");
            return;
        }

        if (entity instanceof Player && !player.hasPermission("seikatsu.hat.player"))
        {
            player.sendMessage(ChatColor.RED + "エラー：プレイヤーをかぶる権限がありません！");
            return;
        }

        if (entity.getVehicle() != null)
        {
            player.sendMessage(ChatColor.RED + "エラー：他のエンティティがかぶっているエンティティをかぶることはできません。");
            return;
        }

        setRide(player, entity);
        player.sendMessage(ChatColor.GREEN + "目の前のエンティティをかぶりました！");
    }

    public static void setRide(Entity target, Entity pass)
    {
        List<Entity> passengers = target.getPassengers();


        if (passengers.size() == 0)
        {
            if (pass.getVehicle() == null)
                target.addPassenger(pass);
            return;
        }

        setRide(passengers.get(passengers.size() - 1), pass);
    }

    public static void hatItem(Player player, ItemStack item)
    {
        if (!Utils.hasPermission(player, "seikatsu.hat.item"))
            return;

        if (item.getAmount() != 1)
        {
            player.sendMessage(ChatColor.RED + "エラー：アイテムは一つ持ってください。");
            return;
        }

        ItemStack helmetItem = player.getInventory().getHelmet();

        player.getInventory().setHelmet(item);

        if (helmetItem != null)
            player.getInventory().setItemInMainHand(helmetItem);
        else
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!sender.hasPermission("seikatsu.hat.item") &&
                !sender.hasPermission("seikatsu.hat.entity") &&
                !sender.hasPermission("seikatsu.hat.player"))
        {
            sender.sendMessage(ChatColor.RED + "エラー：権限がありません！");
            return true;
        }

        if (!Utils.invalidLengthMessage(sender, args, 0, 0) || !Utils.isPlayer(sender))
            return true;

        Player player = (Player) sender;

        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack.getType() != Material.AIR)
        {
            hatItem(player, stack);
            return true;
        }

        LivingEntity entity = Utils.getLookingEntity(player);

        if (entity != null)
        {
            hatEntity(player, entity);
            return true;
        }

        player.sendMessage(ChatColor.RED + "エラー：かぶれるものがありません！");
        return true;
    }
}
