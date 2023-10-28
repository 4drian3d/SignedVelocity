package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import io.github._4drian3d.signedvelocity.fabric.model.SignedChatCommandPacket;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1)
public abstract class ServerGamePacketListenerMixin {
    @Shadow
    public ServerPlayer player;

    @Shadow
    protected abstract void performChatCommand(ServerboundChatCommandPacket serverboundChatCommandPacket, LastSeenMessages lastSeenMessages);

    @Inject(
            method = "performChatCommand",
            at = @At("HEAD"),
            cancellable = true)
    public void signedVelocity$handleChatCommand(
            ServerboundChatCommandPacket packet,
            LastSeenMessages lastSeenMessages,
            CallbackInfo ci
    ) {
        if (((SignedChatCommandPacket)(Object) packet).signedVelocity$handled()) {
            return;
        }
        ((SignedChatCommandPacket)(Object) packet).signedVelocity$handled(true);
        final SignedResult result = SignedVelocity.COMMAND_QUEUE.dataFrom(player.getUUID())
                .nextResult().join();
        // Cancelled Result
        if (result.cancelled()) {
            ci.cancel();
            return;
        }
        final String modified = result.toModify();
        // Modified Result
        if (modified != null) {
            // TODO: verify
            this.performChatCommand(
                    new ServerboundChatCommandPacket(
                            modified,
                            packet.timeStamp(),
                            packet.salt(),
                            packet.argumentSignatures(),
                            packet.lastSeenMessages()
                    ),
                    lastSeenMessages
            );
        }
    }
}
