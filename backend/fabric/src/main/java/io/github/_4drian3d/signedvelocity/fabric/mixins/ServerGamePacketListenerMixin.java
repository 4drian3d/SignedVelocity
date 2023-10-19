package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {
    @ModifyArg(
            at = @At("HEAD"),
            method = "performChatCommand",
            index = 1
    )
    public ServerboundChatCommandPacket signedVelocity$handlePlayerCommand(
            ServerboundChatCommandPacket packet,
            LastSeenMessages lastSeenMessages,
            CallbackInfo ci
    ) {
        final SignedResult result = SignedVelocity.COMMAND_QUEUE.dataFrom(null).nextResult().join();
        if (result.cancelled()) {
            ci.cancel();
        } else {
            final String modified = result.toModify();
            if (modified != null) {
                // TODO: verify
                return new ServerboundChatCommandPacket(
                        modified,
                        packet.timeStamp(),
                        packet.salt(),
                        packet.argumentSignatures(),
                        packet.lastSeenMessages()
                );
            }
        }
        return packet;
    }
}
