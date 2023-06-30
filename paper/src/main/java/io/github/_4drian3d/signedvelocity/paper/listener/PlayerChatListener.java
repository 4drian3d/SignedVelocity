package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.paper.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Objects;

public final class PlayerChatListener implements Listener {
    private final SignedQueue chatQueue;

    public PlayerChatListener(final SignedVelocity plugin) {
        this.chatQueue = plugin.getChatQueue();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(final AsyncChatEvent event) {
        final Player player = event.getPlayer();
        final SignedQueue.SignedResult result = chatQueue.nextResult(player.getUniqueId());
        if (result == null) {
            return;
        }
        if (result.cancelled()) {
            event.setCancelled(true);
        } else {
            event.message(Component.text(Objects.requireNonNull(result.toModify())));
        }
    }
}
