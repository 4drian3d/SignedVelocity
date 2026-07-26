package io.github._4drian3d.signedvelocity.velocity.packet;

import com.google.inject.Injector;
import com.velocitypowered.api.plugin.PluginManager;

import java.util.Map;

public sealed interface PacketAdapter permits PacketEventsAdapter {
  void register();

  static void register(final Injector injector, final PluginManager pluginManager) {
    // VPacketEvents support was dropped in the Velocity 4 port: its adapter
    // required velocity-proxy internals that PaperMC no longer publishes for 4.x.
    final Map<String, Class<? extends PacketAdapter>> adapters = Map.of(
            "packetevents", PacketEventsAdapter.class
            // Probable support of protocolize?
    );
    for (final var adapter : adapters.entrySet()) {
      if (pluginManager.isLoaded(adapter.getKey())) {
        injector.getInstance(adapter.getValue()).register();
        return;
      }
    }
  }
}
