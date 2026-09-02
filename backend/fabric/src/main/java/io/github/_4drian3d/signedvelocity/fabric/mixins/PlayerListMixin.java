package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import io.github._4drian3d.signedvelocity.fabric.model.SignedPlayerChatMessage;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Final;
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

    @Final
    @Shadow
    private MinecraftServer server;

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
            return;
        }
        ((SignedPlayerChatMessage)(Object) message).signedVelocity$handled(true);
        ci.cancel();

        SignedVelocity.CHAT_QUEUE.dataFrom(sender.getUUID())
                .nextResult()
                .thenAccept(result ->
                    server.execute(() -> {
                        if (result.cancelled()) {
                            return;
                        }
                        final String modified = result.message();
                        if (modified != null) {
                            final PlayerChatMessage modifiedMessage = message.withUnsignedContent(Component.literal(modified));
                            ((SignedPlayerChatMessage) (Object) modifiedMessage).signedVelocity$handled(true);
                            this.broadcastChatMessage(modifiedMessage, sender, chatType);
                            return;
                        }
                        this.broadcastChatMessage(message, sender, chatType);
                    })
                );
    }
}
