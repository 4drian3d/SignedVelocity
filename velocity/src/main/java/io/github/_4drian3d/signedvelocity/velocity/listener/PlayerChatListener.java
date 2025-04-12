package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;

import java.util.Objects;

final class PlayerChatListener implements Listener<PlayerChatEvent> {
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
            if (player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) < 0) {
                continuation.resume();
                return;
            }

            if (!result.isAllowed()) {
                return;
            }

            final ServerConnection server = player.getCurrentServer().orElseThrow();

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
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
                    output.writeUTF(player.getUniqueId().toString());
                    output.writeUTF("CHAT_RESULT");
                    output.writeUTF("CANCEL");
                });
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
            server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
                output.writeUTF(player.getUniqueId().toString());
                output.writeUTF("CHAT_RESULT");
                output.writeUTF("MODIFY");
                output.writeUTF(finalMessage);
            });

            continuation.resume();
        });
    }

    @Override
    public void register() {
        eventManager.register(plugin, PlayerChatEvent.class, Short.MIN_VALUE, this);
    }

    private void allowedData(final Player player, final ServerConnection server) {
        server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
            output.writeUTF(player.getUniqueId().toString());
            output.writeUTF("CHAT_RESULT");
            output.writeUTF("ALLOWED");
        });
    }
}