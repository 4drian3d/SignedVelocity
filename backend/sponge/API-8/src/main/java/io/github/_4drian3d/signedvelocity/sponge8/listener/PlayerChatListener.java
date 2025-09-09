package io.github._4drian3d.signedvelocity.sponge8.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.sponge.common.listener.SignedListener;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.plugin.PluginContainer;

import java.util.concurrent.CompletableFuture;

public final class PlayerChatListener implements SignedListener<PlayerChatEvent> {
    @Inject
    @Named("chat")
    private SignedQueue chatQueue;
    @Inject
    private EventManager eventManager;
    @Inject
    private PluginContainer pluginContainer;


    @Override
    public void handle(final PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.context().containsKey(EventContextKeys.SIMULATED_PLAYER)) {
            return;
        }

        event.cause()
                .first(ServerPlayer.class)
                .ifPresent(player -> {
                    final CompletableFuture<SignedResult> futureResult = chatQueue.dataFrom(player.uniqueId()).nextResult();

                    futureResult.thenAccept(result -> {
                        if (result.cancelled()) {
                            event.setCancelled(true);
                        } else {
                            final String modifiedChat = result.toModify();
                            if (modifiedChat != null) {
                                event.setMessage(Component.text(modifiedChat));
                            }
                        }
                    }).join();
                });
    }

    @Override
    public void register() {
        eventManager.registerListener(
                EventListenerRegistration.builder(PlayerChatEvent.class)
                        .listener(this)
                        .plugin(this.pluginContainer)
                        .order(Order.PRE)
                        .beforeModifications(true)
                        .build()
        );
    }
}
