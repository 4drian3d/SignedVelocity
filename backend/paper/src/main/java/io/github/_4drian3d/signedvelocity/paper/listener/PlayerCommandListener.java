package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerCommandListener implements EventListener<PlayerCommandPreprocessEvent> {
    private static final StackWalker WALKER = StackWalker.getInstance();
    private final SignedQueue commandQueue;

    public PlayerCommandListener(final SignedVelocity plugin) {
        this.commandQueue = plugin.getCommandQueue();
    }

    @Override
    public @NotNull EventPriority priority() {
        return EventPriority.LOWEST;
    }

    @Override
    public boolean ignoreCancelled() {
        return true;
    }

    @Override
    public void handle(@NotNull PlayerCommandPreprocessEvent event) {
        if (isLocalCommand()) {
            return;
        }
        final Player player = event.getPlayer();
        this.commandQueue.dataFrom(player.getUniqueId())
                .nextResult()
                .thenAccept(result -> {
                    if (result.cancelled()) {
                        event.setCancelled(true);
                    } else {
                        final String modified = result.toModify();
                        if (modified != null) {
                            event.setMessage(modified);
                        }
                    }
                }).join();
    }

    @Override
    public @NotNull Class<PlayerCommandPreprocessEvent> eventClass() {
        return PlayerCommandPreprocessEvent.class;
    }

    private boolean isLocalCommand() {
        return WALKER.walk(stream -> stream.skip(9)
                .limit(2)
                .map(StackWalker.StackFrame::getMethodName)
                .filter(method -> method.equals("chat") || method.equals("handleCommand"))
                .count() == 2);
    }
}
