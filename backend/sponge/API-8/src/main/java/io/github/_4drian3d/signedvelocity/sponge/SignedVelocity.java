package io.github._4drian3d.signedvelocity.sponge;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.github._4drian3d.signedvelocity.sponge.common.handler.ProxyDataHandler;
import io.github._4drian3d.signedvelocity.sponge.common.listener.PlayerCommandListener;
import io.github._4drian3d.signedvelocity.sponge.common.listener.PlayerQuitListener;
import io.github._4drian3d.signedvelocity.sponge.common.listener.SignedListener;
import io.github._4drian3d.signedvelocity.sponge.common.modules.SignedModule;
import io.github._4drian3d.signedvelocity.sponge.listener.PlayerChatListener;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.network.channel.raw.RawDataChannel;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.util.stream.Stream;

@Plugin("signedvelocity")
public class SignedVelocity {
    @Inject
    private Injector injector;

    @Listener
    public void onEngineStart(final StartedEngineEvent<Server> event) {
        this.injector = this.injector.createChildInjector(new SignedModule());

        Stream.of(PlayerChatListener.class, PlayerCommandListener.class, PlayerQuitListener.class)
                        .map(injector::getInstance)
                        .forEach(SignedListener::register);
        event.game()
                .channelManager()
                .ofType(ResourceKey.of("signedvelocity", "main"), RawDataChannel.class)
                .play()
                .addHandler(injector.getInstance(ProxyDataHandler.class));
    }
}
