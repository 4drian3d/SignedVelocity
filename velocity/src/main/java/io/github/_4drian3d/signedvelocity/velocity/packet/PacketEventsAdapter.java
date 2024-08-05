package io.github._4drian3d.signedvelocity.velocity.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerServerData;

final class PacketEventsAdapter implements PacketAdapter {

  @Override
  public void register() {
    PacketEvents.getAPI()
            .getEventManager()
            .registerListener(new DataListener(), PacketListenerPriority.NORMAL);
  }

  private static final class DataListener implements PacketListener {
    @Override
    public void onPacketSend(final PacketSendEvent event) {
      if (event.getPacketType() == PacketType.Play.Server.SERVER_DATA) {
        final WrapperPlayServerServerData packet = new WrapperPlayServerServerData(event);
        packet.setEnforceSecureChat(true);
      }
      if (event.getPacketType() == PacketType.Play.Server.JOIN_GAME) {
        final WrapperPlayServerJoinGame packet = new WrapperPlayServerJoinGame(event);
        packet.setEnforcesSecureChat(true);
      }
    }
  }
}
