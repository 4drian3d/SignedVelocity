package io.github._4drian3d.signedvelocity.fabric.model;

import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record QueuedDataPacket(
        UUID playerId,
        String source,
        String result,
        @Nullable String modifiedMessage
) implements CustomPacketPayload {
  public static final CustomPacketPayload.Type<QueuedDataPacket> PACKET_ID = new CustomPacketPayload.Type<>(SignedVelocity.CHANNEL);
  public static final StreamCodec<ByteBuf, QueuedDataPacket> PACKET_CODEC = CustomPacketPayload.codec(QueuedDataPacket::write, QueuedDataPacket::generate);

  public static QueuedDataPacket generate(final ByteBuf buf) {
    final FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(buf);
    final UUID playerId = friendlyByteBuf.readUUID();
    final String source = friendlyByteBuf.readUtf();
    final String result = friendlyByteBuf.readUtf();
    final String modifiedMessage = result.equals("MODIFY")
            ? friendlyByteBuf.readUtf()
            : null;
    return new QueuedDataPacket(playerId, source, result, modifiedMessage);
  }

  public static void write(final QueuedDataPacket packet, final ByteBuf buf) {
    final FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(buf);
    friendlyByteBuf.writeUUID(packet.playerId());
    friendlyByteBuf.writeUtf(packet.source);
    friendlyByteBuf.writeUtf(packet.result);
    if (packet.result.equals("MODIFY"))
      friendlyByteBuf.writeUtf(packet.modifiedMessage);
  }

  @Override
  public @NotNull Type<? extends CustomPacketPayload> type() {
    return PACKET_ID;
  }
}
