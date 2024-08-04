package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Injector;
import com.velocitypowered.api.event.AwaitingEventExecutor;

public sealed interface Listener<E> extends AwaitingEventExecutor<E> permits PlayerChatListener, PlayerCommandListener, PluginMessageListener {
    void register();

    static void register(final Injector injector) {
        final Listener<?>[] listeners = {
                injector.getInstance(PlayerChatListener.class),
                injector.getInstance(PlayerCommandListener.class),
                injector.getInstance(PluginMessageListener.class)
        };
        for (final Listener<?> listener : listeners) {
            listener.register();
        }
    }
}
