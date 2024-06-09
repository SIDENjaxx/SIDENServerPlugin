package net.toaru.sidenplugin;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsSayEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final CommandSender sender;
    private final String as;
    private final String text;

    public AsSayEvent(CommandSender sender, String as, String text) {
        this.sender = sender;
        this.as = as;
        this.text = text;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getAs() {
        return as;
    }

    public String getText() {
        return text;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
