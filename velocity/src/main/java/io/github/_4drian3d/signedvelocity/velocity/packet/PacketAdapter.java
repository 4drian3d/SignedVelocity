package io.github._4drian3d.signedvelocity.velocity.packet;

import com.google.inject.Injector;
import com.velocitypowered.api.plugin.PluginManager;

import java.util.Map;

public sealed interface PacketAdapter permits PacketEventsAdapter, VPacketEventsAdapter {
  void register();

  static void register(final Injector injector, final PluginManager pluginManager) {
    final Map<String, Class<? extends PacketAdapter>> adapters = Map.of(
            // TODO: Re-enable when PacketEvents fixes its PostOrder problem on initialization
            //"packetevents", PacketEventsAdapter.class,
            "vpacketevents", VPacketEventsAdapter.class
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
