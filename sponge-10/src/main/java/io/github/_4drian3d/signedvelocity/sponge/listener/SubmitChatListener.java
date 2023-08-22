package io.github._4drian3d.signedvelocity.sponge.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.common.SignedResult;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.plugin.PluginContainer;

import java.util.concurrent.CompletableFuture;

public final class SubmitChatListener implements SignedListener<PlayerChatEvent.Submit> {
    @Inject
    @Named("chat")
    private SignedQueue chatQueue;
    @Inject
    private EventManager eventManager;
    @Inject
    private PluginContainer pluginContainer;


    @Override
    public void handle(final PlayerChatEvent.Submit event) {
        if (event.isCancelled()) {
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
                            if (modifiedChat != null && !event.isSigned()) {
                                event.setMessage(Component.text(modifiedChat));
                            }
                        }
                    }).join();
                });
    }

    @Override
    public void register() {
        eventManager.registerListener(
                EventListenerRegistration.builder(PlayerChatEvent.Submit.class)
                        .listener(this)
                        .plugin(this.pluginContainer)
                        .order(Order.PRE)
                        .beforeModifications(true)
                        .build()
        );
    }
}
