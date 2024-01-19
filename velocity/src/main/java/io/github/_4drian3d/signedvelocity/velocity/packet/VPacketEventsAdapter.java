package io.github._4drian3d.signedvelocity.velocity.packet;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.proxy.protocol.packet.ServerDataPacket;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import io.github._4drian3d.vpacketevents.api.event.PacketSendEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

final class VPacketEventsAdapter implements PacketAdapter {
  private static final MethodHandle ENFORCED_SETTER;

  static {
    try {
      final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ServerDataPacket.class, MethodHandles.lookup());
      ENFORCED_SETTER = lookup.findSetter(ServerDataPacket.class, "secureChatEnforced", Boolean.TYPE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Inject
  private EventManager eventManager;
  @Inject
  private SignedVelocity plugin;

  @Override
  public void register() {
    eventManager.register(plugin, PacketSendEvent.class, event -> {
      if (!(event.getPacket() instanceof final ServerDataPacket serverData)) {
        return;
      }
      if (serverData.isSecureChatEnforced()) {
        return;
      }
      try {
        ENFORCED_SETTER.invoke(serverData, true);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    });
  }
}
