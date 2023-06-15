package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public final class PlayerCommandEvent implements Listener<CommandExecuteEvent> {
    @Inject
    private EventManager eventManager;
    @Inject
    private SignedVelocity plugin;

    @Override
    public void register() {
        eventManager.register(plugin, CommandExecuteEvent.class, PostOrder.LAST, this);
    }

    @Override
    public @Nullable EventTask executeAsync(final CommandExecuteEvent event) {
        final CommandExecuteEvent.CommandResult result = event.getResult();
        if (result == CommandExecuteEvent.CommandResult.allowed() || result.isForwardToServer()) return null;
        if (!(event.getCommandSource() instanceof Player player)) return null;
        return EventTask.async(() -> {
            final String originalCommand = event.getCommand();
            final RegisteredServer server = player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .orElseThrow();
            final String finalCommand = event.getResult().getCommand().orElse(null);

            if (finalCommand == null) {
                // Cancelled
                final ByteArrayDataOutput buf = ByteStreams.newDataOutput();
                buf.writeUTF("COMMAND_RESULT");
                buf.writeUTF("CANCEL");
                buf.writeUTF(player.getUsername());
                final byte[] data = buf.toByteArray();
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
            } else if (!Objects.equals(originalCommand, finalCommand)) {
                // Modified
                final ByteArrayDataOutput buf = ByteStreams.newDataOutput();
                buf.writeUTF("COMMAND_RESULT");
                buf.writeUTF("MODIFY");
                buf.writeUTF(player.getUsername());
                buf.writeUTF(finalCommand);
            }
            event.setResult(CommandExecuteEvent.CommandResult.allowed());
        });
    }
}
