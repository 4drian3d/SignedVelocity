package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, priority = 100)
public abstract class PlayerListMixin {
    @ModifyArg(
            at = @At(value = "HEAD"),
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            index = 1
    )
    private PlayerChatMessage signedVelocity$onPlayerChat(
            PlayerChatMessage playerChatMessage,
            ServerPlayer serverPlayer,
            ChatType.Bound bound,
            CallbackInfo ci
    ) {
        if (serverPlayer == null) {
            return playerChatMessage;
        }
        final SignedResult result = SignedVelocity.CHAT_QUEUE.dataFrom(serverPlayer.getUUID()).nextResult().join();
        if (result.cancelled()) {
            ci.cancel();
        } else {
            final String modifiedChat = result.toModify();
            if (modifiedChat != null) {
                return playerChatMessage.withUnsignedContent(Component.literal(modifiedChat));
            }
        }
        return playerChatMessage;
    }
}
