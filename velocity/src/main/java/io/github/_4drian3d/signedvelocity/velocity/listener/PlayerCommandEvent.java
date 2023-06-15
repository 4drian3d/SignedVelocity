package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.signedvelocity.velocity.DataBuilder;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import org.checkerframework.checker.nullness.qual.Nullable;

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
            final RegisteredServer server = player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .orElseThrow();
            final String finalCommand = event.getResult().getCommand().orElse(null);

            if (finalCommand == null) {
                // Cancelled
                final DataBuilder builder = DataBuilder
                        .builder()
                        .append("COMMAND_RESULT")
                        .append("CANCEL")
                        .append(player.getUsername());
                final byte[] data = builder.build();
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
            } else {
                // Modified
                final DataBuilder builder = DataBuilder
                        .builder()
                        .append("COMMAND_RESULT")
                        .append("MODIFY")
                        .append(player.getUsername())
                        .append(finalCommand);
                final byte[] data = builder.build();
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, data);
            }
            event.setResult(CommandExecuteEvent.CommandResult.allowed());
        });
    }
}
