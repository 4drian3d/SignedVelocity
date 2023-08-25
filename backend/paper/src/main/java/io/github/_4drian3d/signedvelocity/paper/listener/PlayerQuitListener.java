package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PlayerQuitListener implements Listener {
    private final SignedQueue chatQueue;
    private final SignedQueue commandQueue;

    public PlayerQuitListener(final SignedVelocity plugin) {
        this.chatQueue = plugin.getChatQueue();
        this.commandQueue = plugin.getCommandQueue();
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        this.chatQueue.removeData(playerUUID);
        this.commandQueue.removeData(playerUUID);
    }
}
