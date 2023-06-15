package io.github._4drian3d.signedvelocity.paper;

import io.github._4drian3d.signedvelocity.paper.listener.PlayerChatListener;
import io.github._4drian3d.signedvelocity.paper.listener.PlayerCommandListener;
import io.github._4drian3d.signedvelocity.paper.listener.PluginMessagingListener;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignedVelocity extends JavaPlugin {
    public static final String CHANNEL = "signedvelocity:main";
    private final SignedQueue chatQueue = new SignedQueue();
    private final SignedQueue commandQueue = new SignedQueue();

    @Override
    public void onEnable() {
        final Server server = getServer();
        server.getMessenger().registerIncomingPluginChannel(this, CHANNEL, new PluginMessagingListener(this));
        server.getPluginManager().registerEvents(new PlayerChatListener(this), this);
        server.getPluginManager().registerEvents(new PlayerCommandListener(this), this);
    }

    public SignedQueue getChatQueue() {
        return chatQueue;
    }

    public SignedQueue getCommandQueue() {
        return commandQueue;
    }
}
