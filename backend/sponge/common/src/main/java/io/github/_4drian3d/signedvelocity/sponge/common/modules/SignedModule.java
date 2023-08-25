package io.github._4drian3d.signedvelocity.sponge.common.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.github._4drian3d.signedvelocity.common.SignedQueue;

public final class SignedModule extends AbstractModule {
    private final SignedQueue chatQueue = new SignedQueue();
    private final SignedQueue commandQueue = new SignedQueue();

    @Override
    protected void configure() {
        this.bind(SignedQueue.class)
                .annotatedWith(Names.named("chat"))
                .toInstance(this.chatQueue);
        this.bind(SignedQueue.class)
                .annotatedWith(Names.named("command"))
                .toInstance(this.commandQueue);
    }
}
