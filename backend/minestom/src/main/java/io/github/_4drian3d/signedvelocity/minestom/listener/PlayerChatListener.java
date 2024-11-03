package io.github._4drian3d.signedvelocity.minestom.listener;

import io.github._4drian3d.signedvelocity.minestom.SignedVelocity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;

import java.util.function.Consumer;

public final class PlayerChatListener implements Consumer<PlayerChatEvent> {

    @Override
    public void accept(final PlayerChatEvent event) {
        final Player player = event.getPlayer();
        SignedVelocity.chatQueue().dataFrom(player.getUuid())
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
