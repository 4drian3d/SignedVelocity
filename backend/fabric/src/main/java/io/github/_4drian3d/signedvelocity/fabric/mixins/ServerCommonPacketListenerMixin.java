package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ServerCommonPacketListenerImpl.class, priority = 10)
public abstract class ServerCommonPacketListenerMixin {
    @Inject(at = @At("HEAD"), method = "handleCustomPayload", cancellable = true)
    private void signedVelocity$onPluginMessage(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (packet.payload() instanceof PacketByteBufPayload payload && payload.id().equals(SignedVelocity.CHANNEL)) {
            final FriendlyByteBuf input = payload.data();
            final UUID playerId = UUID.fromString(input.readUtf());
            final String source = input.readUtf();
            final String result = input.readUtf();

            final SignedQueue queue = switch (source) {
                case "COMMAND_RESULT" -> SignedVelocity.COMMAND_QUEUE;
                case "CHAT_RESULT" -> SignedVelocity.CHAT_QUEUE;
                default -> throw new IllegalArgumentException("Invalid source " + source);
            };
            final SignedResult resulted = switch (result) {
                case "CANCEL" -> SignedResult.cancel();
                case "MODIFY" -> SignedResult.modify(input.readUtf());
                case "ALLOWED" -> SignedResult.allowed();
                default -> throw new IllegalArgumentException("Invalid result " + result);
            };
            queue.dataFrom(playerId).complete(resulted);
            ci.cancel();
        }
    }
}