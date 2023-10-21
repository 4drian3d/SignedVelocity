package io.github._4drian3d.signedvelocity.fabric.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github._4drian3d.signedvelocity.common.SignedResult;
import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 100)
public abstract class ServerGamePacketListenerModifiableMixin {
    @Shadow
    public ServerPlayer player;

    @ModifyVariable(
            at = @At("HEAD"),
            method = "performChatCommand",
            index = 1,
            argsOnly = true)
    public ServerboundChatCommandPacket signedVelocity$handlePlayerCommand(
            ServerboundChatCommandPacket packet,
            @Local LastSeenMessages lastSeenMessages
    ) {
        final SignedResult result = SignedVelocity.COMMAND_QUEUE.dataFrom(player.getUUID())
                .nextResultWithoutAdvance().join();
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
        return packet;
    }
}
