package io.github._4drian3d.signedvelocity.paper;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.listener.*;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignedVelocity extends JavaPlugin {
    public static final String CHANNEL = "signedvelocity:main";
    private final SignedQueue chatQueue = new SignedQueue();
    private final SignedQueue commandQueue = new SignedQueue();

    @Override
    public void onEnable() {
        final Server server = getServer();
        server.getMessenger().registerIncomingPluginChannel(this, CHANNEL, new PluginMessagingListener(this));

        final PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(new DecorateChatListener(this), this);
        pluginManager.registerEvents(new PlayerChatListener(this), this);
        pluginManager.registerEvents(new PlayerCommandListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(this), this);
    }

    public SignedQueue getChatQueue() {
        return chatQueue;
    }

    public SignedQueue getCommandQueue() {
        return commandQueue;
    }
}
