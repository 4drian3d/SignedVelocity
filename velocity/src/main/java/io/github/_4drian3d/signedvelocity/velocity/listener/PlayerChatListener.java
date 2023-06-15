package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
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
        return EventTask.async(() -> {
            final Player player = event.getPlayer();
            final String originalMessage = event.getMessage();
            final RegisteredServer server = player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .orElseThrow();
            final String finalMessage = event.getResult().getMessage().orElse(null);

            if (finalMessage == null) {
                // Cancelled
                final ByteArrayDataOutput buf = ByteStreams.newDataOutput();
                buf.writeUTF("CHAT_RESULT");
                buf.writeUTF("CANCEL");
                buf.writeUTF(player.getUsername());
                final byte[] data = buf.toByteArray();
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
            } else if (!Objects.equals(originalMessage, finalMessage)) {
                // Modified
                final ByteArrayDataOutput buf = ByteStreams.newDataOutput();
                buf.writeUTF("CHAT_RESULT");
                buf.writeUTF("MODIFY");
                buf.writeUTF(player.getUsername());
                buf.writeUTF(finalMessage);
            }
            event.setResult(PlayerChatEvent.ChatResult.allowed());
        });
    }

    @Override
    public void register() {
        eventManager.register(plugin, PlayerChatEvent.class, PostOrder.LAST, this);
    }
}