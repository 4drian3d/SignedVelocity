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

            // Denied
            // | The player has an old version, so you can safely deny execution from Velocity
            if (!result.isAllowed() && player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) < 0) {
                continuation.resume();
                return;
            }

            final RegisteredServer server = player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .orElseThrow();

            // Allowed
            // | If the message is allowed simply transmit that should be accepted
            if (result == PlayerChatEvent.ChatResult.allowed()) {
                allowedData(player, server);
                continuation.resume();
                return;
            }

            event.setResult(PlayerChatEvent.ChatResult.allowed());

            final String finalMessage = result.getMessage().orElse(null);

            // Cancelled
            // | The result is to cancel the execution
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

            // ALLOWED
            // | If the result of the event is to modify the message,
            // | but the modified message is the same as the executed one, simply accept the execution
            if (Objects.equals(finalMessage, event.getMessage())) {
                allowedData(player, server);
                continuation.resume();
                return;
            }

            // Modified
            // | The result is to modify the command
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