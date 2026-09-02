package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
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
    public abstract void broadcastChatMessage(PlayerChatMessage message, ServerPlayer sender, ChatType.Bound chatType);

    @SuppressWarnings({"DataFlowIssue", "UnreachableCode"})
    @Inject(
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            at = @At("HEAD"), cancellable = true)
    public void signedVelocity$handleChat(
            PlayerChatMessage message,
            ServerPlayer sender,
            ChatType.Bound chatType,
            CallbackInfo ci
    ) {
        requireNonNull(sender);
        if (((SignedPlayerChatMessage)(Object) message).signedVelocity$handled()) {
            System.out.println("SignedVelocity: Chat message already handled, skipping.");
            return;
        }
        ((SignedPlayerChatMessage)(Object) message).signedVelocity$handled(true);
        final SignedResult result = SignedVelocity.CHAT_QUEUE.dataFrom(sender.getUUID())
                .nextResult().join();
        // Cancelled Result
        if (result.cancelled()) {
            System.out.println("SignedVelocity: Chat message was cancelled.");
            ci.cancel();
            return;
        }
        final String modified = result.message();
        // Modified Result
        if (modified != null) {
            System.out.println("SignedVelocity: Chat message was modified.");
            this.broadcastChatMessage(message.withUnsignedContent(Component.literal(modified)), sender, chatType);
            ci.cancel();
        }
        System.out.println("SignedVelocity: Chat message was allowed.");
    }
}
