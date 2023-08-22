package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

public final class PlayerChatListener implements Listener {
    private final SignedQueue chatQueue;

    public PlayerChatListener(final SignedVelocity plugin) {
        this.chatQueue = plugin.getChatQueue();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncChatEvent event) {
        final Player player = event.getPlayer();
        final CompletableFuture<SignedResult> futureResult = chatQueue.dataFrom(player.getUniqueId()).nextResult();

        futureResult.thenAccept(result -> {
            if (result.cancelled()) {
                event.setCancelled(true);
            } else {
                final String modifiedChat = result.toModify();
                if (modifiedChat != null) {
                    event.message(Component.text(modifiedChat));
                }
            }
        }).join();
    }
}
