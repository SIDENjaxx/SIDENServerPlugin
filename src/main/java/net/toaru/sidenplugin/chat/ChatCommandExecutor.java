package net.toaru.sidenplugin.chat;

public interface ChatCommandExecutor
{
    String getName();

    void onExecute(ChatCommandComponent component);
}
