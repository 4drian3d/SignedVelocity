package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.paper.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Objects;

public final class PlayerCommandListener implements Listener {
    private final SignedQueue commandQueue;

    public PlayerCommandListener(final SignedVelocity plugin) {
        this.commandQueue = plugin.getCommandQueue();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommand(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final SignedQueue.SignedResult result = commandQueue.nextResult(player.getUniqueId());
        if (result == null) {
            return;
        }
        if (result.cancelled()) {
            event.setCancelled(true);
        } else {
            event.setMessage(Objects.requireNonNull(result.toModify()));
        }
    }
}
