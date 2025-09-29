package io.github._4drian3d.signedvelocity.fabric;

import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.shared.SignedConstants;
import io.github._4drian3d.signedvelocity.fabric.model.QueuedDataPacket;
import io.github._4drian3d.signedvelocity.shared.types.QueueType;
import io.github._4drian3d.signedvelocity.shared.types.ResultType;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SignedVelocity implements DedicatedServerModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("SignedVelocity");
  public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(
      SignedConstants.SIGNED_NAMESPACE, SignedConstants.SIGNED_CHANNEL);
  public static final SignedQueue CHAT_QUEUE = new SignedQueue();
  public static final SignedQueue COMMAND_QUEUE = new SignedQueue();

  @Override
  public void onInitializeServer() {
    ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
      final var uuid = handler.getPlayer().getUUID();
      CHAT_QUEUE.removeData(uuid);
      COMMAND_QUEUE.removeData(uuid);
    });
    PayloadTypeRegistry.playC2S().register(QueuedDataPacket.PACKET_ID, QueuedDataPacket.PACKET_CODEC);
    ServerPlayNetworking.registerGlobalReceiver(QueuedDataPacket.PACKET_ID, (packet, context) -> {
      final SignedQueue queue = switch (QueueType.getOrThrow(packet.source())) {
        case QueueType.COMMAND -> SignedVelocity.COMMAND_QUEUE;
        case QueueType.CHAT -> SignedVelocity.CHAT_QUEUE;
      };
      final SignedResult resulted = switch (ResultType.getOrThrow(packet.result())) {
        case ResultType.CANCEL -> SignedResult.cancel();
        case ResultType.MODIFY -> SignedResult.modify(packet.modifiedMessage());
        case ResultType.ALLOWED -> SignedResult.allowed();
      };
      queue.dataFrom(packet.playerId()).complete(resulted);
    });

    LOGGER.info("Started SignedVelocity");
  }
}