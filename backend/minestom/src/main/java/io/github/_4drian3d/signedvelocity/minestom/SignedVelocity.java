package io.github._4drian3d.signedvelocity.minestom;

import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.minestom.listener.PlayerChatListener;
import io.github._4drian3d.signedvelocity.minestom.listener.PlayerCommandListener;
import io.github._4drian3d.signedvelocity.minestom.listener.PluginMessageListener;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public final class SignedVelocity {
    public static final String CHANNEL = "signedvelocity:main";
    private static final SignedQueue chatQueue = new SignedQueue();
    private static final SignedQueue commandQueue = new SignedQueue();

    public static void initialize() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerChatEvent.class, new PlayerChatListener());
        MinecraftServer.getGlobalEventHandler().addListener(PlayerCommandEvent.class, new PlayerCommandListener());
        MinecraftServer.getGlobalEventHandler().addListener(PlayerPluginMessageEvent.class, new PluginMessageListener());
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event -> {
            final @NotNull var uuid = event.getPlayer().getUuid();
            commandQueue.removeData(uuid);
            chatQueue.removeData(uuid);
        });
    }

    public static SignedQueue chatQueue() {
        return chatQueue;
    }

    public static SignedQueue commandQueue() {
        return commandQueue;
    }
}
