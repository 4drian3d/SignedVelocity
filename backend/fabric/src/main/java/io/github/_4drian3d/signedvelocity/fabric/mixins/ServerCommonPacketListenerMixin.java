package io.github._4drian3d.signedvelocity.fabric.mixins;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 10)
public abstract class ServerCommonPacketListenerMixin {
    @Inject(at = @At("HEAD"), method = "handleCustomPayload", cancellable = true)
    private void signedVelocity$onPluginMessage(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (packet.getIdentifier().equals(SignedVelocity.CHANNEL)) {
            final ByteArrayDataInput input = ByteStreams.newDataInput(packet.getData().accessByteBufWithCorrectSize());
            final UUID playerId = UUID.fromString(input.readUTF());
            final String source = input.readUTF();
            final String result = input.readUTF();

            final SignedQueue queue = switch (source) {
                case "COMMAND_RESULT" -> SignedVelocity.COMMAND_QUEUE;
                case "CHAT_RESULT" -> SignedVelocity.CHAT_QUEUE;
                default -> throw new IllegalArgumentException("Invalid source " + source);
            };
            final SignedResult resulted = switch (result) {
                case "CANCEL" -> SignedResult.cancel();
                case "MODIFY" -> SignedResult.modify(input.readUTF());
                case "ALLOWED" -> SignedResult.allowed();
                default -> throw new IllegalArgumentException("Invalid result " + result);
            };
            queue.dataFrom(playerId).complete(resulted);
            ci.cancel();
        }
    }
}