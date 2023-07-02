package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.signedvelocity.velocity.DataBuilder;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;

import java.util.Objects;

public final class PlayerChatListener implements Listener<PlayerChatEvent> {
    @Inject
    private EventManager eventManager;
    @Inject
    private SignedVelocity plugin;

    @Override
    public EventTask executeAsync(final PlayerChatEvent event) {
        final PlayerChatEvent.ChatResult result = event.getResult();

        return EventTask.withContinuation(continuation -> {
            final Player player = event.getPlayer();
            final RegisteredServer server = player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .orElseThrow();

            // Allowed
            if (result == PlayerChatEvent.ChatResult.allowed()) {
                allowedData(player, server);
                continuation.resume();
                return;
            }

            event.setResult(PlayerChatEvent.ChatResult.allowed());

            // Allowed
            if (player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) < 0) {
                allowedData(player, server);
                continuation.resume();
                return;
            }

            final String finalMessage = event.getResult().getMessage().orElse(null);

            // Cancelled
            if (finalMessage == null) {
                final DataBuilder builder = DataBuilder
                        .builder()
                        .append(player.getUniqueId().toString())
                        .append("CHAT_RESULT")
                        .append("CANCEL");
                final byte[] data = builder.build();
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
                continuation.resume();
                return;
            }

            // Allowed
            if (Objects.equals(finalMessage, event.getMessage())) {
                allowedData(player, server);
                continuation.resume();
                return;
            }

            // Modified
            final DataBuilder builder = DataBuilder
                    .builder()
                    .append(player.getUniqueId().toString())
                    .append("CHAT_RESULT")
                    .append("MODIFY")
                    .append(finalMessage);
            final byte[] data = builder.build();
            server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);

            continuation.resume();
        });
    }

    @Override
    public void register() {
        eventManager.register(plugin, PlayerChatEvent.class, PostOrder.LAST, this);
    }

    private void allowedData(Player player, RegisteredServer server) {
        final DataBuilder builder = DataBuilder
                .builder()
                .append(player.getUniqueId().toString())
                .append("CHAT_RESULT")
                .append("ALLOWED");
        final byte[] data = builder.build();
        server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
    }
}