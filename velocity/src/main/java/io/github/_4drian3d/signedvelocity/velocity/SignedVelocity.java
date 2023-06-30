package io.github._4drian3d.signedvelocity.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.github._4drian3d.signedvelocity.velocity.listener.Listener;
import io.github._4drian3d.signedvelocity.velocity.listener.PlayerChatListener;
import io.github._4drian3d.signedvelocity.velocity.listener.PlayerCommandEvent;
import org.slf4j.Logger;

import java.util.stream.Stream;

@Plugin(
        id = "signedvelocity",
        authors = { "4drian3d" },
        version = Constants.VERSION
)
public final class SignedVelocity {
    public static final ChannelIdentifier SIGNEDVELOCITY_CHANNEL = MinecraftChannelIdentifier.create(
      "signedvelocity", "main"
    );

    @Inject
    private Injector injector;
    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        logger.info("Starting SignedVelocity");
        Stream.of(
                PlayerChatListener.class,
                PlayerCommandEvent.class
        ).map(injector::getInstance).forEach(Listener::register);
    }
}