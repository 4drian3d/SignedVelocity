package io.github._4drian3d.signedvelocity.minestom.listener;

import io.github._4drian3d.signedvelocity.minestom.SignedVelocity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerCommandEvent;

import java.util.function.Consumer;

public final class PlayerCommandListener implements Consumer<PlayerCommandEvent> {

    @Override
    public void accept(final PlayerCommandEvent event) {
        final Player player = event.getPlayer();
        SignedVelocity.commandQueue().dataFrom(player.getUuid())
                .nextResult()
                .thenAccept(result -> {
                    if (result.cancelled()) {
                        event.setCancelled(true);
                    } else {
                        final String modified = result.toModify();
                        if (modified != null) {
                            event.setCommand(modified);
                        }
                    }
                }).join();
    }
}
