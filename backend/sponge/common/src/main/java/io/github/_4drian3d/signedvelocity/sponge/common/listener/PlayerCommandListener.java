package io.github._4drian3d.signedvelocity.sponge.common.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.SignedQueue;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.plugin.PluginContainer;

public final class PlayerCommandListener implements SignedListener<ExecuteCommandEvent.Pre> {
    @Inject
    @Named("command")
    private SignedQueue commandQueue;
    @Inject
    private EventManager eventManager;
    @Inject
    private PluginContainer pluginContainer;


    @Override
    public void handle(final ExecuteCommandEvent.Pre event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.context().containsKey(EventContextKeys.SIMULATED_PLAYER)) {
            return;
        }

        event.cause()
                .first(ServerPlayer.class)
                .ifPresent(player -> this.commandQueue.dataFrom(player.uniqueId())
                        .nextResult()
                        .thenAccept(result -> {
                            if (result.cancelled()) {
                                event.setCancelled(true);
                            } else {
                                final String modified = result.toModify();
                                if (modified != null) {
                                    event.setCommand(modified);
                                }
                            }
                        }).join());
    }

    @Override
    public void register() {
        eventManager.registerListener(
                EventListenerRegistration.builder(ExecuteCommandEvent.Pre.class)
                        .listener(this)
                        .plugin(this.pluginContainer)
                        .order(Order.PRE)
                        .beforeModifications(true)
                        .build()
        );
    }
}
