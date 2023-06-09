package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.paper.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedResult;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class PlayerCommandListener implements Listener {
    private final SignedQueue commandQueue;

    public PlayerCommandListener(final SignedVelocity plugin) {
        this.commandQueue = plugin.getCommandQueue();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommand(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final CompletableFuture<SignedResult> futureResult = commandQueue.dataFrom(player.getUniqueId()).nextResult();

        futureResult.completeOnTimeout(SignedResult.allowed(), 150, TimeUnit.MILLISECONDS).thenAccept(result -> {
            if (result.cancelled()) {
                event.setCancelled(true);
            } else {
                final String modified = result.toModify();
                if (modified != null) {
                    event.setMessage(modified);
                }
            }
        }).join();
    }
}
