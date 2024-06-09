package net.toaru.sidenplugin.events;

import net.toaru.sidenplugin.commands.CommandUnHat;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HatEventListener implements Listener
{
    @EventHandler
    public void onDamageEvent(EntityDamageEvent e)
    {
        if (e.getCause() == EntityDamageEvent.DamageCause.FLY_INTO_WALL)
            if (e.getEntity().getVehicle() != null && !(e.getEntity().getVehicle() instanceof Minecart || e.getEntity().getVehicle() instanceof Boat))
                e.setCancelled(true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e)
    {
        CommandUnHat.unHatEntities(e.getPlayer());
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e)
    {
        if (e.getEntity().getVehicle() == null)
            return;

        if (e.getTarget() == null)
            return;
        if (e.getTarget().getUniqueId().equals(e.getEntity().getVehicle().getUniqueId()))
            e.setCancelled(true);
    }


}
