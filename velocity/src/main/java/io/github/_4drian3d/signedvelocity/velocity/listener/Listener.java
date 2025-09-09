package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Injector;
import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import io.github._4drian3d.signedvelocity.velocity.types.SignedResult;

public sealed interface Listener<E> extends AwaitingEventExecutor<E> permits PlayerChatListener, PlayerCommandListener, PluginMessageListener, PostPlayerCommandListener {
    void register();

    static void register(final Injector injector) {
        final Listener<?>[] listeners = {
                injector.getInstance(PlayerChatListener.class),
                injector.getInstance(PlayerCommandListener.class),
                injector.getInstance(PostPlayerCommandListener.class),
                injector.getInstance(PluginMessageListener.class)
        };
        for (final Listener<?> listener : listeners) {
            listener.register();
        }
    }

    default void allowedData(final Player player, final ServerConnection server, SignedResult result) {
        server.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
            output.writeUTF(player.getUniqueId().toString());
            output.writeUTF(result.toString());
            output.writeUTF("ALLOWED");
        });
    }
}
