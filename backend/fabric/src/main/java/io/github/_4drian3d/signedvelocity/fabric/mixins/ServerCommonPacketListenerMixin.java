package io.github._4drian3d.signedvelocity.fabric.mixins;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.fabricmc.fabric.impl.networking.payload.RetainedPayload;
import net.fabricmc.fabric.impl.networking.payload.UntypedPayload;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ServerCommonPacketListenerImpl.class, priority = 10)
public abstract class ServerCommonPacketListenerMixin {
    @Inject(at = @At("HEAD"), method = "handleCustomPayload", cancellable = true)
    private void signedVelocity$onPluginMessage(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (!(packet.payload() instanceof RetainedPayload payload)) {
            return;
        }
        if (!payload.id().equals(SignedVelocity.CHANNEL)) {
            return;
        }
        final UntypedPayload resolved = (UntypedPayload) payload.resolve(null);
        final ByteArrayDataInput input = ByteStreams.newDataInput(resolved.buffer().array());
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