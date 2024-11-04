package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

final class PlayerCommandListener implements Listener<CommandExecuteEvent> {
    @Inject
    private EventManager eventManager;
    @Inject
    private CommandManager commandManager;
    @Inject
    private SignedVelocity plugin;

    @Override
    public void register() {
        eventManager.register(plugin, CommandExecuteEvent.class, Short.MIN_VALUE, this);
    }

    @Override
    public @Nullable EventTask executeAsync(final CommandExecuteEvent event) {
        final CommandExecuteEvent.CommandResult result = event.getResult();

        if (!(event.getCommandSource() instanceof Player player)) return null;
        return EventTask.withContinuation(continuation -> {
            final RegisteredServer server = player.getCurrentServer()
                    .map(ServerConnection::getServer)
                    .orElse(null);

            // The player is not connected to a server, there is nothing I can do.
            if (server == null) {
                continuation.resume();
                return;
            }

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

            final String finalCommand = result.getCommand().orElse(null);

            // Cancelled
            // | The result is to cancel the execution
            if (finalCommand == null) {
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
                    output.writeUTF(player.getUniqueId().toString());
                    output.writeUTF("COMMAND_RESULT");
                    output.writeUTF("CANCEL");
                });
                event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
                continuation.resume();
                return;
            }

            // ALLOWED
            // | If the result of the event is to modify the command,
            // | but the modified command is the same as the executed one, simply accept the execution
            if (Objects.equals(finalCommand, event.getCommand())) {
                allowedData(player, server);
                event.setResult(CommandExecuteEvent.CommandResult.allowed());
                continuation.resume();
                return;
            }

            // Modified
            // | The result is to modify the command
            server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
                output.writeUTF(player.getUniqueId().toString());
                output.writeUTF("COMMAND_RESULT");
                output.writeUTF("MODIFY");
                output.writeUTF(finalCommand);
            });
            if (this.isProxyCommand(event.getCommand())) {
                event.setResult(CommandExecuteEvent.CommandResult.command(finalCommand));
            } else {
                event.setResult(CommandExecuteEvent.CommandResult.forwardToServer(finalCommand));
            }
            continuation.resume();
        });
    }

    private void allowedData(final Player player, final RegisteredServer server) {
        server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
            output.writeUTF(player.getUniqueId().toString());
            output.writeUTF("COMMAND_RESULT");
            output.writeUTF("ALLOWED");
        });
    }

    private boolean isProxyCommand(final String command) {
        final int firstIndexOfSpace = command.indexOf(' ');

        return switch (firstIndexOfSpace) {
            // If the command has no spaces
            case -1 -> this.commandManager.hasCommand(command);
            // In case the command executed is for example "/      test asd"
            case 0 -> {
                final String[] arguments = command.split(" ");
                // All blanks are filtered out until the first argument is reached
                for (final String argument : arguments) {
                    if (argument.isBlank()) continue;
                    yield this.commandManager.hasCommand(argument);
                }
                final String firstArgument = command.substring(0, firstIndexOfSpace);
                yield this.commandManager.hasCommand(firstArgument);
            }
            // Normal execution with multiple arguments "/test asd"
            default ->  {
                final String firstArgument = command.substring(0, firstIndexOfSpace);
                yield this.commandManager.hasCommand(firstArgument);
            }
        };
    }
}
