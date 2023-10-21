package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 101)
public abstract class ServerGamePacketListenerCancellableMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(
            method = "performChatCommand",
            at = @At("HEAD"),
            cancellable = true)
    private void signedVelocity$performCancellableCommand(
            ServerboundChatCommandPacket serverboundChatCommandPacket,
            LastSeenMessages lastSeenMessages,
            CallbackInfo ci
    ) {
        final SignedResult result = SignedVelocity.COMMAND_QUEUE.dataFrom(player.getUUID())
                .nextResult().join();
        if (result.cancelled()) {
            ci.cancel();
        }
    }
}
