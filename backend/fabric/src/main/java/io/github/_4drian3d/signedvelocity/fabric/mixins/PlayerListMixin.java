package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import io.github._4drian3d.signedvelocity.fabric.model.SignedPlayerChatMessage;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.util.Objects.requireNonNull;

@Mixin(value = PlayerList.class, priority = 1)
public abstract class PlayerListMixin {
    @Shadow
    public abstract void broadcastChatMessage(PlayerChatMessage playerChatMessage, ServerPlayer serverPlayer, ChatType.Bound bound);

    @Inject(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At("HEAD"), cancellable = true)
    public void signedVelocity$handleChat(
            PlayerChatMessage playerChatMessage,
            ServerPlayer serverPlayer,
            ChatType.Bound bound,
            CallbackInfo ci
    ) {
        requireNonNull(serverPlayer);
        if (!((SignedPlayerChatMessage)(Object)playerChatMessage).signedVelocity$handled()) {
            ((SignedPlayerChatMessage)(Object)playerChatMessage).signedVelocity$handled(true);
            final SignedResult result = SignedVelocity.CHAT_QUEUE.dataFrom(serverPlayer.getUUID())
                    .nextResult().join();
            // Cancelled Result
            if (result.cancelled()) {
                ci.cancel();
                return;
            }
            final String modified = result.message();
            // Modified Result
            if (modified != null) {
                this.broadcastChatMessage(playerChatMessage.withUnsignedContent(Component.literal(modified)), serverPlayer, bound);
            }
        }
    }
}
