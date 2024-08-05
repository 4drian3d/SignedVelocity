package io.github._4drian3d.signedvelocity.velocity.packet;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.proxy.protocol.packet.JoinGamePacket;
import com.velocitypowered.proxy.protocol.packet.ServerDataPacket;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import io.github._4drian3d.vpacketevents.api.event.PacketSendEvent;

final class VPacketEventsAdapter implements PacketAdapter {

  @Inject
  private EventManager eventManager;
  @Inject
  private SignedVelocity plugin;

  @Override
  public void register() {
    eventManager.register(plugin, PacketSendEvent.class, event -> {
      if (event.getPacket() instanceof final ServerDataPacket serverData) {
        serverData.setSecureChatEnforced(true);
      }
      if (event.getPacket() instanceof final JoinGamePacket joinPacket) {
        joinPacket.setEnforcesSecureChat(true);
      }
    });
  }
}
