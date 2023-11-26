package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.common.logger.DebugLogger;
import io.github._4drian3d.signedvelocity.common.queue.QueuedData;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText;

public final class PlayerChatListener implements EventListener<AsyncChatEvent>, LocalExecutionDetector {
    private final SignedQueue chatQueue;
    private final DebugLogger debugLogger;

    public PlayerChatListener(final SignedVelocity plugin) {
        this.chatQueue = plugin.getChatQueue();
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
    public void handle(final @NotNull AsyncChatEvent event) {
        debugLogger.debug(() -> "[CHAT] Init Message Handling | Received on: "+System.currentTimeMillis());
        if (CHECK_FOR_LOCAL_CHAT && (!event.isAsynchronous() || isLocal())) {
            debugLogger.debug(() -> "[CHAT] Local Message Executed");
            return;
        }
        final Player player = event.getPlayer();
        debugLogger.debug(() -> "[CHAT] Queueing Next Result");
        final QueuedData data = this.chatQueue.dataFrom(player.getUniqueId());
        final CompletableFuture<SignedResult> nextResult = data.nextResult();
        debugLogger.debug(() -> "[CHAT] Future Done: " + nextResult.isDone());
        // In case the chat has really been executed from the player,
        // but some plugin has cancelled it by means of a deprecated event,
        // simply let the queue advance, there is nothing to do
        if (event.isCancelled()) {
            debugLogger.debug(() -> "[CHAT] Deprecated Event Cancelled");
            return;
        }

        debugLogger.debug(() -> "[CHAT] Waiting for next result");

        nextResult.thenAccept(result -> {
            debugLogger.debug(() -> "[CHAT] Next Result");
            if (result.cancelled()) {
                debugLogger.debugMultiple(() -> new String[] {
                        "[CHAT] Cancelled Message.",
                        "Original Message: " + plainText().serialize(event.message())
                });
                event.setCancelled(true);
            } else {
                final String modifiedChat = result.toModify();
                if (modifiedChat != null) {
                    debugLogger.debugMultiple(() -> new String[] {
                            "[CHAT] Modified message",
                            "Original: " + plainText().serialize(event.message()),
                            "Message: " + modifiedChat
                    });
                    event.message(Component.text(modifiedChat));
                }
            }
            debugLogger.debug(() -> "[CHAT] Result applied");
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
