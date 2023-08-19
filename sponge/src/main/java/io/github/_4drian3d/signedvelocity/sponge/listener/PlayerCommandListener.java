package io.github._4drian3d.signedvelocity.sponge.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.common.SignedResult;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventListenerRegistration;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.plugin.PluginContainer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
        event.cause().first(ServerPlayer.class)
                .ifPresent(player -> {
                    final CompletableFuture<SignedResult> futureResult = commandQueue.dataFrom(player.uniqueId()).nextResult();

                    futureResult.completeOnTimeout(SignedResult.allowed(), 150, TimeUnit.MILLISECONDS).thenAccept(result -> {
                        if (result.cancelled()) {
                            event.setCancelled(true);
                        } else {
                            final String modified = result.toModify();
                            if (modified != null) {
                                event.setCommand(modified);
                            }
                        }
                    }).join();
                });
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
