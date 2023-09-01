package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class PlayerQuitListener implements EventListener<PlayerQuitEvent> {
    private final SignedQueue chatQueue;
    private final SignedQueue commandQueue;

    public PlayerQuitListener(final SignedVelocity plugin) {
        this.chatQueue = plugin.getChatQueue();
        this.commandQueue = plugin.getCommandQueue();
    }

    @Override
    public @NotNull EventPriority priority() {
        return EventPriority.NORMAL;
    }

    @Override
    public boolean ignoreCancelled() {
        // well...
        return true;
    }

    @Override
    public void handle(@NotNull PlayerQuitEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        this.chatQueue.removeData(playerUUID);
        this.commandQueue.removeData(playerUUID);
    }

    @Override
    public @NotNull Class<PlayerQuitEvent> eventClass() {
        return PlayerQuitEvent.class;
    }
}
