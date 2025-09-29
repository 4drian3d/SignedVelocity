package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.shared.logger.DebugLogger;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerCommandListener implements EventListener<PlayerCommandPreprocessEvent>, LocalExecutionDetector {
    private final SignedQueue commandQueue;
    private final DebugLogger debugLogger;

    public PlayerCommandListener(final SignedVelocity plugin) {
        this.commandQueue = plugin.getCommandQueue();
        this.debugLogger = plugin.debugLogger();
    }

    @Override
    public @NotNull EventPriority priority() {
        return EventPriority.LOWEST;
    }

    @Override
    public boolean ignoreCancelled() {
        return false;
    }

    @Override
    public void handle(final @NotNull PlayerCommandPreprocessEvent event) {
        if (CHECK_FOR_LOCAL_CHAT && isLocal()) {
            debugLogger.debug(() -> "[COMMAND] Local Command Executed");
            return;
        }

        final Player player = event.getPlayer();
        this.commandQueue.dataFrom(player.getUniqueId())
                .nextResult()
                .thenAccept(result -> {
                    if (result.cancelled()) {
                        debugLogger.debug(() -> "[COMMAND] Canceled Command. Command: " + event.getMessage());
                        event.setCancelled(true);
                    } else {
                        final String modified = result.toModify();
                        if (modified != null) {
                            debugLogger.debugMultiple(() -> new String[] {
                                    "[COMMAND] Modified Command",
                                    "Original: " + event.getMessage(),
                                    "Modified: " + modified
                            });
                            event.setMessage(modified);
                        }
                    }
                }).join();
    }

    @Override
    public @NotNull Class<PlayerCommandPreprocessEvent> eventClass() {
        return PlayerCommandPreprocessEvent.class;
    }

    @Override
    public boolean isLocal() {
        return WALKER.walk(stream -> stream.limit(15)
                .map(StackWalker.StackFrame::getMethodType)
                .noneMatch(method -> {
                    final var parameters = method.parameterList();
                    return parameters.contains(ServerboundChatCommandPacket.class)
                            || parameters.contains(ServerboundChatCommandSignedPacket.class);
                }));
    }
}
