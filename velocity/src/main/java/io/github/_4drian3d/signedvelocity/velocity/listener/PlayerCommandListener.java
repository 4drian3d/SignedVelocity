package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import io.github._4drian3d.signedvelocity.velocity.cache.ModificationCache;
import io.github._4drian3d.signedvelocity.velocity.types.SignedResult;
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
        eventManager.register(plugin, CommandExecuteEvent.class, (short)-32760, this);
    }

    @Override
    public @Nullable EventTask executeAsync(final CommandExecuteEvent event) {
        final CommandExecuteEvent.InvocationInfo invocationInfo = event.getInvocationInfo();
        // A plugin command invocation, like CommandManager#executeAsync(CommandSource, String)
        if (invocationInfo.source() == CommandExecuteEvent.Source.API) return null;
        // A non-player command invocation
        if (!(event.getCommandSource() instanceof Player player)) return null;

        return EventTask.withContinuation(continuation -> {
            final ServerConnection server = player.getCurrentServer().orElse(null);

            // The player is not connected to a server, there is nothing I can do.
            if (server == null) {
                plugin.logDebug("Command Execution | Null Server");
                continuation.resume();
                return;
            }

            final CommandExecuteEvent.CommandResult result = event.getResult();
            final String finalCommand = result.getCommand().orElse(null);

            // ALLOWED
            // | If the command will be redirected to the server,
            // | simply transmit that the command should be accepted
            if (result.isForwardToServer()) {
                plugin.logDebug("Command Execution | Forward to Server");
                // If the command is sent to the server but modified,
                // it is sent as unmodified and then modified on the backend server
                if (finalCommand != null) {
                    plugin.logDebug("Command Execution | Signed Command Executed, modified and forwarded");
                    event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
                    // Modified
                    // | Modified Command but forwarded to backend server
                    server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
                        output.writeUTF(player.getUniqueId().toString());
                        output.writeUTF("COMMAND_RESULT");
                        output.writeUTF("MODIFY");
                        output.writeUTF(finalCommand);
                    });
                    continuation.resume();
                    return;
                }
                plugin.logDebug("Command Execution | Command Forwarded to server");
                // If the command has not been modified, it is simply allowed to be executed regularly
                allowedData(player, server, SignedResult.COMMAND_RESULT);
                continuation.resume();
                return;
            }


            final boolean isProxyCommand = this.isProxyCommand(event.getCommand());
            // ALLOWED
            // | Direct command allowed
            if (result == CommandExecuteEvent.CommandResult.allowed() || Objects.equals(finalCommand, event.getCommand())) {
                plugin.logDebug("Command Execution | Allowed Command");
                // If it is detected that it is a command registered in Velocity,
                // it delegates the sending of the SignedResult to the PostPlayerCommandListener to see
                // if it is necessary to send it or if it was executed entirely in the Velocity command dispatcher
                if (!isProxyCommand) {
                    plugin.logDebug("Command Execution | Allowed non proxied command");
                    //allowedData(player, server, SignedResult.COMMAND_RESULT);
                }
                // If the command is registered in Velocity,
                // then its execution is delegated to Velocity's CommandDispatcher.
                // From there, if the command is sent to the backend server via a RRigadierCommand.FORWARD,
                // this is detected by the PostPlayerCommandListener.
                continuation.resume();
                return;
            }

            // DENIED
            // | The player has an old version, so you can safely deny execution from Velocity
            if (!result.isAllowed() && player.getProtocolVersion().lessThan(ProtocolVersion.MINECRAFT_1_19_1)) {
                plugin.logDebug("Command Execution | Old player version, denied command");
                event.setResult(CommandExecuteEvent.CommandResult.denied());
                continuation.resume();
                return;
            }

            // Cancelled
            // | The result is to cancel the execution
            if (finalCommand == null) {
                plugin.logDebug("Command Execution | Cancelled command execution");
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
                    output.writeUTF(player.getUniqueId().toString());
                    output.writeUTF("COMMAND_RESULT");
                    output.writeUTF("CANCEL");
                });
                // The command can be sent securely to the backend server,
                // thus preventing the Velocity dispatcher from trying to execute it,
                // and the backend server denies its execution before the backend dispatcher recognizes it
                event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
                continuation.resume();
                return;
            }

            // --- Modification Section ---
            plugin.logDebug("Command Execution | Modification Section");
            if (!isProxyCommand) {
                plugin.logDebug("Command Execution | Non proxied command");
                // Modified
                // | If the command is not registered in Velocity,
                // | the dispatcher is prevented from even attempting to execute it
                server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
                    output.writeUTF(player.getUniqueId().toString());
                    output.writeUTF("COMMAND_RESULT");
                    output.writeUTF("MODIFY");
                    output.writeUTF(finalCommand);
                });
                // The command is passed as if it had not been modified so that the backend server can safely modify it
                event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
                continuation.resume();
                return;
            }

            plugin.logDebug("Command Execution | Modified Command sent to Velocity Command Dispatcher");

            // If the command is modified in Velocity, it has a command registered in Velocity,
            // but when executed it is sent to the backend server,
            // there may be problems in certain versions of the backend server.
            event.setResult(CommandExecuteEvent.CommandResult.command(finalCommand));
            plugin.modificationCache().put(player.getUniqueId().toString(), new ModificationCache(event.getCommand(), finalCommand));
            continuation.resume();
        });
    }

    private boolean isProxyCommand(final String command) {
        final int firstIndexOfSpace = command.indexOf(' ');

        return switch (firstIndexOfSpace) {
            // If the command has no spaces
            case -1 -> commandManager.hasCommand(command);
            // In case the command executed is for example "/      test asd"
            case 0 -> {
                final String[] arguments = command.split(" ");
                // All blanks are filtered out until the first argument is reached
                for (final String argument : arguments) {
                    if (argument.isBlank()) continue;
                    yield commandManager.hasCommand(argument);
                }
                final String firstArgument = command.substring(0, firstIndexOfSpace);
                yield commandManager.hasCommand(firstArgument);
            }
            // Normal execution with multiple arguments "/test asd"
            default ->  {
                final String firstArgument = command.substring(0, firstIndexOfSpace);
                yield commandManager.hasCommand(firstArgument);
            }
        };
    }
}
