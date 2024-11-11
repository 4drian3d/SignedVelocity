package io.github._4drian3d.signedvelocity.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.github._4drian3d.signedvelocity.velocity.listener.Listener;
import io.github._4drian3d.signedvelocity.velocity.packet.PacketAdapter;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

@Plugin(
        id = "signedvelocity",
        authors = {"4drian3d"},
        version = Constants.VERSION,
        description = "Allows you to cancel or modify messages or commands from Velocity without synchronization problems",
        dependencies = {
                @Dependency(id = "vpacketevents", optional = true),
                @Dependency(id = "packetevents", optional = true)
        }
)
public final class SignedVelocity {
  public static final ChannelIdentifier SIGNEDVELOCITY_CHANNEL = MinecraftChannelIdentifier.create(
          "signedvelocity", "main"
  );

  @Inject
  private Injector injector;
  @Inject
  private Logger logger;
  @Inject
  private Metrics.Factory factory;
  @Inject
  private PluginManager pluginManager;

  @Subscribe
  public void onProxyInitialization(final ProxyInitializeEvent event) {
    factory.make(this, 18937);

    logger.info("Starting SignedVelocity");

    Listener.register(injector);
    PacketAdapter.register(injector, pluginManager);
  }
}