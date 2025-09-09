package io.github._4drian3d.signedvelocity.velocity;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.github._4drian3d.signedvelocity.velocity.cache.ModificationCache;
import io.github._4drian3d.signedvelocity.velocity.listener.Listener;
import io.github._4drian3d.signedvelocity.velocity.packet.PacketAdapter;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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
  private static final boolean DEBUG = Boolean.getBoolean("io.github._4drian3d.signedvelocity.debug");
  public static final ChannelIdentifier SIGNEDVELOCITY_CHANNEL = MinecraftChannelIdentifier.create(
          "signedvelocity", "main"
  );
  private final Cache<String, ModificationCache> modificationCache = Caffeine.newBuilder()
          .expireAfterWrite(Duration.of(1, ChronoUnit.SECONDS))
          .build();

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

  public Cache<String, ModificationCache> modificationCache() {
    return modificationCache;
  }

  public void logDebug(String string) {
    if (DEBUG) {
      logger.info("SIGNEDVELOCITY DEBUG | {}", string);
    }
  }
}