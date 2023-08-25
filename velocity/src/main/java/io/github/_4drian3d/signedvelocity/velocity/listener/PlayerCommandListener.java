package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.signedvelocity.velocity.DataBuilder;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public final class PlayerCommandListener implements Listener<CommandExecuteEvent> {
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

        if (!(event.getCommandSource() instanceof Player player)) return null;
        return EventTask.withContinuation(continuation -> {
            final RegisteredServer server = player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .orElseThrow();
            // ALLOWED
            // | If the command is allowed or will be redirected to the server,
            // | simply transmit that the command should be accepted
            if (result == CommandExecuteEvent.CommandResult.allowed() || result.isForwardToServer()) {
                allowedData(player, server);
                continuation.resume();
                return;
            }

            // DENIED
            // | The player has an old version, so you can safely deny execution from Velocity
            if (!result.isAllowed() && player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) < 0) {
                continuation.resume();
                return;
            }
            event.setResult(CommandExecuteEvent.CommandResult.allowed());

            final String finalCommand = result.getCommand().orElse(null);

            // Cancelled
            // | The result is to cancel the execution
            if (finalCommand == null) {
                final DataBuilder builder = DataBuilder
                        .builder()
                        .append(player.getUniqueId().toString())
                        .append("COMMAND_RESULT")
                        .append("CANCEL");
                final byte[] data = builder.build();
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
                continuation.resume();
                return;
            }

            // ALLOWED
            // | If the result of the event is to modify the command,
            // | but the modified command is the same as the executed one, simply accept the execution
            if (Objects.equals(finalCommand, event.getCommand())) {
                allowedData(player, server);
                continuation.resume();
                return;
            }

            // Modified
            // | The result is to modify the command
            final DataBuilder builder = DataBuilder
                    .builder()
                    .append(player.getUniqueId().toString())
                    .append("COMMAND_RESULT")
                    .append("MODIFY")
                    .append(finalCommand);
            final byte[] data = builder.build();
            server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
            continuation.resume();
        });
    }

    private void allowedData(Player player, RegisteredServer server) {
        final DataBuilder builder = DataBuilder
                .builder()
                .append(player.getUniqueId().toString())
                .append("COMMAND_RESULT")
                .append("ALLOWED");
        final byte[] data = builder.build();
        server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
    }
}
