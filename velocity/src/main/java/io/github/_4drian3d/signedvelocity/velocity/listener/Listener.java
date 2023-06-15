package io.github._4drian3d.signedvelocity.velocity.listener;

import com.velocitypowered.api.event.AwaitingEventExecutor;

public sealed interface Listener<E> extends AwaitingEventExecutor<E> permits PlayerChatListener, PlayerCommandEvent {
    void register();
}
