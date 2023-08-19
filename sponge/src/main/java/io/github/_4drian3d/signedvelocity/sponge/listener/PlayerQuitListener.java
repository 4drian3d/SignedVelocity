package io.github._4drian3d.signedvelocity.sponge.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.SignedQueue;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.plugin.PluginContainer;

import java.util.UUID;

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
                        .order(Order.PRE)
                        .beforeModifications(true)
                        .build()
        );
    }

    @Override
    public void handle(ServerSideConnectionEvent.Disconnect event) {
        final UUID playerUUID = event.player().uniqueId();
        this.chatQueue.removeData(playerUUID);
        this.commandQueue.removeData(playerUUID);
    }
}
