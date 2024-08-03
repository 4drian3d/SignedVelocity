package io.github._4drian3d.signedvelocity.fabric;

import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.model.QueuedDataPacket;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SignedVelocity implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SignedVelocity");
    public static final ResourceLocation CHANNEL = new ResourceLocation.of("signedvelocity", "main");
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
            final SignedQueue queue = switch (packet.source()) {
                case "COMMAND_RESULT" -> SignedVelocity.COMMAND_QUEUE;
                case "CHAT_RESULT" -> SignedVelocity.CHAT_QUEUE;
                default -> throw new IllegalArgumentException("Invalid source " + packet.source());
            };
            final SignedResult resulted = switch (packet.result()) {
                case "CANCEL" -> SignedResult.cancel();
                case "MODIFY" -> SignedResult.modify(packet.modifiedMessage());
                case "ALLOWED" -> SignedResult.allowed();
                default -> throw new IllegalArgumentException("Invalid result " + packet.result());
            };
            queue.dataFrom(packet.playerId()).complete(resulted);
        });

        LOGGER.info("Started SignedVelocity");
    }
}
