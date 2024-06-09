package net.toaru.sidenplugin.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface ChatCommandComponent
{
    String[] getArgs();

    Player getPlayer();

    boolean isCancelled();

    void setCancelled(boolean cancel);

    Component getComponent();

    void setComponent(Component component);

}
