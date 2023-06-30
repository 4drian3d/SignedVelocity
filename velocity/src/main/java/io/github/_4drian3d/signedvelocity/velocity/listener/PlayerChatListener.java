package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.signedvelocity.velocity.DataBuilder;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public final class PlayerChatListener implements Listener<PlayerChatEvent> {
    @Inject
    private EventManager eventManager;
    @Inject
    private SignedVelocity plugin;

    @Override
    public @Nullable EventTask executeAsync(final PlayerChatEvent event) {
        final PlayerChatEvent.ChatResult result = event.getResult();
        if (result == PlayerChatEvent.ChatResult.allowed()) return null;
        return EventTask.withContinuation(continuation -> {
            final Player player = event.getPlayer();
            final RegisteredServer server = player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .orElseThrow();
            final String finalMessage = event.getResult().getMessage().orElse(null);

            if (Objects.equals(finalMessage, event.getMessage())) {
                continuation.resume();
                return;
            }

            if (finalMessage == null) {
                // Cancelled
                final DataBuilder builder = DataBuilder
                        .builder()
                        .append("CHAT_RESULT")
                        .append("CANCEL")
                        .append(player.getUniqueId().toString());
                final byte[] data = builder.build();
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
            } else {
                // Modified
                final DataBuilder builder = DataBuilder
                        .builder()
                        .append("CHAT_RESULT")
                        .append("MODIFY")
                        .append(player.getUniqueId().toString())
                        .append(finalMessage);
                final byte[] data = builder.build();
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
            }
            event.setResult(PlayerChatEvent.ChatResult.allowed());
            continuation.resume();
        });
    }

    @Override
    public void register() {
        eventManager.register(plugin, PlayerChatEvent.class, PostOrder.LAST, this);
    }
}