package io.github._4drian3d.signedvelocity.sponge.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.sponge.common.listener.SignedListener;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.plugin.PluginContainer;

public final class DecorateChatListener implements SignedListener<PlayerChatEvent.Decorate> {
    @Inject
    @Named("chat")
    private SignedQueue chatQueue;
    @Inject
    private EventManager eventManager;
    @Inject
    private PluginContainer pluginContainer;

    @Override
    public void handle(final PlayerChatEvent.Decorate event) {
        if (event.context().containsKey(EventContextKeys.SIMULATED_PLAYER)) {
            return;
        }

        event.cause()
                .first(ServerPlayer.class)
                .ifPresent(player -> this.chatQueue.dataFrom(player.uniqueId())
                        .nextResultWithoutAdvance()
                        .thenAccept(result -> {
                            final String modifiedChat = result.toModify();
                            if (modifiedChat != null) {
                                event.setMessage(Component.text(modifiedChat));
                            }
                        }).join());
    }

    @Override
    public void register() {
        eventManager.registerListener(
                EventListenerRegistration.builder(PlayerChatEvent.Decorate.class)
                        .listener(this)
                        .plugin(this.pluginContainer)
                        .order(Order.PRE)
                        .beforeModifications(true)
                        .build()
        );
    }
}
