package net.toaru.sidenplugin.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ChatCommand
{
    private final List<ChatCommandExecutor> commands;
    private final List<String> commandNames;
    private final String _PREFIX;

    private final ChatCommand instance;

    public ChatCommand(String prefix, Plugin plugin)
    {
        commands = new ArrayList<>();
        commandNames = new ArrayList<>();
        _PREFIX = prefix;
        instance = this;
    }

    public ChatCommandComponent onCommand(Player player, String command, Component cp)
    {
        if (!command.startsWith(_PREFIX))
            return null;

        String commandName;
        String[] args;
        int space = command.indexOf(" ");
        if (space != -1)
        {
            commandName = command.substring(_PREFIX.length(), space);
            args = StringUtils.split(command.substring(commandName.length(), ' '));
        }
        else
        {
            commandName = command.substring(1);
            args = new String[]{};
        }

        if (!commandNames.contains(commandName))
            return null;

        int index = commandNames.indexOf(commandName);

        ChatCommandComponent component = new CommandComponentBuilder(args, player, cp);

        commands.get(index).onExecute(component);

        return component;
    }

    public void registerCommand(ChatCommandExecutor command)
    {
        commands.add(command);
        commandNames.add(command.getName());
    }

    public void removeCommand(String name)
    {
        int at = commandNames.indexOf(name);
        commands.remove(at);
        commandNames.remove(name);
    }

    private static class CommandComponentBuilder implements ChatCommandComponent
    {

        private final String[] args;
        private final Player player;
        private boolean cancelled;

        private Component component;

        public CommandComponentBuilder(String[] args, Player player, Component component)
        {
            this.args = args;
            this.player = player;
            this.cancelled = false;
            this.component = component;
        }

        @Override
        public String[] getArgs()
        {
            return args;
        }

        @Override
        public Player getPlayer()
        {
            return player;
        }

        @Override
        public boolean isCancelled()
        {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel)
        {
            this.cancelled = cancel;
        }

        @Override
        public Component getComponent()
        {
            return component;
        }

        @Override
        public void setComponent(Component component)
        {
            this.component = component;
        }
    }

    private class EventListener implements Listener
    {
        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onChat(AsyncChatEvent e)
        {
            ChatCommandComponent comp = instance.onCommand(e.getPlayer(), ((TextComponent) e.message()).content(), e.message());
            if (comp == null)
                return;
            e.setCancelled(comp.isCancelled());
            e.message(comp.getComponent());
        }
    }
}
