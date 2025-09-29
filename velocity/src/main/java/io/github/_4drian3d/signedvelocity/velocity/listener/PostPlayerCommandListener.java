package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandResult;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.command.PostCommandInvocationEvent;
import com.velocitypowered.api.proxy.Player;
import io.github._4drian3d.signedvelocity.shared.types.QueueType;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import io.github._4drian3d.signedvelocity.velocity.cache.ModificationCache;
import org.jetbrains.annotations.NotNull;

public final class PostPlayerCommandListener implements Listener<@NotNull PostCommandInvocationEvent> {
    @Inject
    private SignedVelocity plugin;
    @Inject
    private EventManager eventManager;

    @Override
    public void register() {
        this.eventManager.register(plugin, PostCommandInvocationEvent.class, Short.MIN_VALUE, this);
    }

    @Override
    public EventTask executeAsync(PostCommandInvocationEvent event) {
        return EventTask.async(() -> {
            // If the command was executed in the CommandDispatcher
            // but was configured to be re-executed on the backend server,
            // the result is sent for processing immediately.
            if (event.getResult() == CommandResult.FORWARDED && event.getCommandSource() instanceof Player player) {
                plugin.logDebug("Post Command Execution | Forwarded Command: " + event.getCommand());
                final String playerUUID = player.getUniqueId().toString();
                final ModificationCache cache = plugin.modificationCache().getIfPresent(playerUUID);
                plugin.modificationCache().invalidate(playerUUID);
                player.getCurrentServer()
                        .ifPresent(connection -> {
                            plugin.logDebug("Post Command Execution | Server Available");
                            if (cache != null && cache.modifiedCommand().equals(event.getCommand())) {
                                plugin.logDebug("Post Command Execution | Modified Command");
                                this.sendModifiedData(player, connection, QueueType.COMMAND, event.getCommand());
                            } else {
                                plugin.logDebug("Post Command Execution | Non modified command");
                                sendAllowedData(player, connection, QueueType.COMMAND);
                            }
                        });
            }
        });
    }
}
