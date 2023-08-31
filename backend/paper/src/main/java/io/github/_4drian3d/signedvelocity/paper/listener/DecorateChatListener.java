package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class DecorateChatListener implements Listener {
    private final SignedQueue chatQueue;

    public DecorateChatListener(final SignedVelocity plugin) {
        this.chatQueue = plugin.getChatQueue();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(final AsyncChatDecorateEvent event) {
        final Player player = event.player();
        if (player == null) {
            return;
        }
        this.chatQueue.dataFrom(player.getUniqueId())
                .nextResultWithoutAdvance()
                .thenAccept(result -> {
                    if (!result.cancelled()) {
                        final String modifiedChat = result.toModify();
                        if (modifiedChat != null) {
                            event.result(Component.text(modifiedChat));
                        }
                    }
                }).join();
    }
}
