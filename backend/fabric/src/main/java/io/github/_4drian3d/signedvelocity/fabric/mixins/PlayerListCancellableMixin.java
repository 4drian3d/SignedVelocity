package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, priority = 101)
public abstract class PlayerListCancellableMixin {
    @Inject(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void signedVelocity$handleCancellableChat(PlayerChatMessage playerChatMessage, ServerPlayer serverPlayer, ChatType.Bound bound, CallbackInfo ci) {
        if (serverPlayer != null) {
            System.out.println("Entry to Modify");
            final SignedResult result = SignedVelocity.CHAT_QUEUE.dataFrom(serverPlayer.getUUID()).nextResult().join();
            if (result.cancelled()) {
                System.out.println("Modify");
                ci.cancel();
            }
        }
    }
}
