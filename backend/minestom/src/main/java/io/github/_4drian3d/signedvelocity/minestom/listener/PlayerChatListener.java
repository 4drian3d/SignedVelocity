package io.github._4drian3d.signedvelocity.minestom.listener;

import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.minestom.SignedVelocity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;

import java.util.function.Consumer;

public final class PlayerChatListener implements Consumer<PlayerChatEvent> {
    private final SignedQueue chatQueue;

    public PlayerChatListener(final SignedVelocity extension) {
        this.chatQueue = extension.chatQueue();
    }

    @Override
    public void accept(final PlayerChatEvent event) {
        final Player player = event.getPlayer();
        this.chatQueue.dataFrom(player.getUuid())
                .nextResult()
                .thenAccept(result -> {
                    if (result.cancelled()) {
                        event.setCancelled(true);
                    } else {
                        final String modifiedChat = result.toModify();
                        if (modifiedChat != null) {
                            event.setMessage(modifiedChat);
                        }
                    }
                }).join();
    }
}
