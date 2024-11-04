package io.github._4drian3d.signedvelocity.minestom;

import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.minestom.listener.PlayerChatListener;
import io.github._4drian3d.signedvelocity.minestom.listener.PlayerCommandListener;
import io.github._4drian3d.signedvelocity.minestom.listener.PluginMessageListener;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.MinecraftServer;

/**
 * Main SignedVelocity class
 */
public final class SignedVelocity {
    public static final String CHANNEL = "signedvelocity:main";
    private static final SignedQueue chatQueue = new SignedQueue();
    private static final SignedQueue commandQueue = new SignedQueue();

    /**
     * Initializes SignedVelocity Minestom on your environment
     */
    public static void initialize() {
        final GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerChatEvent.class, new PlayerChatListener());
        globalEventHandler.addListener(PlayerCommandEvent.class, new PlayerCommandListener());
        globalEventHandler.addListener(PlayerPluginMessageEvent.class, new PluginMessageListener());
        globalEventHandler.addListener(PlayerDisconnectEvent.class, event -> {
            final var uuid = event.getPlayer().getUuid();
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
