package io.github._4drian3d.signedvelocity.fabric.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = PlayerList.class, priority = 100)
public abstract class PlayerListModifiableMixin {
    @ModifyVariable(
            at = @At(value = "HEAD"),
            method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/network/chat/ChatType$Bound;)V",
            argsOnly = true)
    private PlayerChatMessage signedVelocity$onPlayerChat(
            PlayerChatMessage playerChatMessage,
            @Local ServerPlayer serverPlayer
    ) {
        if (serverPlayer == null) {
            return playerChatMessage;
        }
        System.out.println("Entry to Modify");
        final SignedResult result = SignedVelocity.CHAT_QUEUE.dataFrom(serverPlayer.getUUID()).nextResultWithoutAdvance().join();
        final String modifiedChat = result.toModify();
        if (modifiedChat != null) {
            System.out.println("Modified");
            return playerChatMessage.withUnsignedContent(Component.literal(modifiedChat));
        }
        return playerChatMessage;
    }
}
