package io.github._4drian3d.signedvelocity.sponge14;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.github._4drian3d.signedvelocity.sponge10.listener.DecorateChatListener;
import io.github._4drian3d.signedvelocity.sponge10.listener.SubmitChatListener;
import io.github._4drian3d.signedvelocity.sponge14.handler.ProxyDataHandler;
import io.github._4drian3d.signedvelocity.sponge.common.listener.PlayerCommandListener;
import io.github._4drian3d.signedvelocity.sponge.common.listener.SignedListener;
import io.github._4drian3d.signedvelocity.sponge.common.modules.SignedModule;
import io.github._4drian3d.signedvelocity.sponge14.listener.PlayerQuitListener;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.network.EngineConnectionState;
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

        Stream.of(SubmitChatListener.class, DecorateChatListener.class, PlayerCommandListener.class, PlayerQuitListener.class)
                .map(injector::getInstance)
                .forEach(SignedListener::register);
        event.game()
                .channelManager()
                .ofType(ResourceKey.of("signedvelocity", "main"), RawDataChannel.class)
                .play()
                .addHandler(EngineConnectionState.Game.class, injector.getInstance(ProxyDataHandler.class));
    }
}
