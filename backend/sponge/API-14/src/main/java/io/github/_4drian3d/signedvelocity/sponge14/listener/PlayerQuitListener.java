package io.github._4drian3d.signedvelocity.sponge14.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.sponge.common.listener.SignedListener;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.network.EngineConnectionState;
import org.spongepowered.plugin.PluginContainer;

import java.util.Optional;

public final class PlayerQuitListener implements SignedListener<ServerSideConnectionEvent.Disconnect> {
    @Inject
    @Named("chat")
    private SignedQueue chatQueue;
    @Inject
    @Named("command")
    private SignedQueue commandQueue;
    @Inject
    private EventManager eventManager;
    @Inject
    private PluginContainer pluginContainer;

    @Override
    public void register() {
        eventManager.registerListener(
                EventListenerRegistration.builder(ServerSideConnectionEvent.Disconnect.class)
                        .listener(this)
                        .plugin(this.pluginContainer)
                        .order(Order.DEFAULT)
                        .beforeModifications(false)
                        .build()
        );
    }

    @Override
    public void handle(final ServerSideConnectionEvent.Disconnect event) {
        event.connection()
                .state()
                .flatMap(state -> state instanceof EngineConnectionState.Authenticated auth
                        ? Optional.of(auth.profile().uniqueId()) : Optional.empty())
                .ifPresent(playerUUID -> {
                    this.chatQueue.removeData(playerUUID);
                    this.commandQueue.removeData(playerUUID);
                });
    }
}
