package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public final class DecorateChatListener implements EventListener<AsyncChatDecorateEvent> {
    private final SignedQueue chatQueue;

    public DecorateChatListener(final SignedVelocity plugin) {
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
    public void handle(@NotNull AsyncChatDecorateEvent event) {
        final Player player = event.player();
        if (player == null) {
            return;
        }
        this.chatQueue.dataFrom(player.getUniqueId())
                .nextResultWithoutAdvance()
                .thenAccept(result -> {
                    if (!result.cancelled()) {
                        final String modifiedChat = result.toModify();
                        if (modifiedChat != null) {
                            event.result(Component.text(modifiedChat));
                        }
                    }
                }).join();
    }

    @Override
    public @NotNull Class<AsyncChatDecorateEvent> eventClass() {
        return AsyncChatDecorateEvent.class;
    }
}
