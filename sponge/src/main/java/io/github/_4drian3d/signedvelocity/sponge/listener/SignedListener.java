package io.github._4drian3d.signedvelocity.sponge.listener;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

public interface SignedListener<E extends Event> extends EventListener<E> {
    void register();
}
