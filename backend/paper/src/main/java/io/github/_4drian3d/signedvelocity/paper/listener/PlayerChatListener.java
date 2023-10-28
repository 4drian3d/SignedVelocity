package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public final class PlayerChatListener implements EventListener<AsyncChatEvent>, LocalExecutionDetector {
    private final SignedQueue chatQueue;

    public PlayerChatListener(final SignedVelocity plugin) {
        this.chatQueue = plugin.getChatQueue();
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
    public void handle(final @NotNull AsyncChatEvent event) {
        if (CHECK_FOR_LOCAL_CHAT && isLocal()) {
            return;
        }
        final Player player = event.getPlayer();
        this.chatQueue.dataFrom(player.getUniqueId())
                .nextResult()
                .thenAccept(result -> {
                    if (result.cancelled()) {
                        event.setCancelled(true);
                    } else {
                        final String modifiedChat = result.toModify();
                        if (modifiedChat != null) {
                            event.message(Component.text(modifiedChat));
                        }
                    }
                }).join();
    }

    @Override
    public @NotNull Class<AsyncChatEvent> eventClass() {
        return AsyncChatEvent.class;
    }

    @Override
    public boolean isLocal() {
        return StackWalker.getInstance()
                .walk(stream -> stream.skip(12)
                        .limit(2)
                        .map(StackWalker.StackFrame::getMethodName)
                        .filter(method -> method.equals("chat"))
                        .count() == 2);
    }
}
