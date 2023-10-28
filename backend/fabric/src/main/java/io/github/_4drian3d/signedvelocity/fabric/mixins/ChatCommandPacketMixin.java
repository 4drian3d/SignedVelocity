package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.fabric.model.SignedChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerboundChatCommandPacket.class)
public class ChatCommandPacketMixin implements SignedChatCommandPacket {
    @Unique
    private boolean signedVelocity$handled = false;

    @Override
    public boolean signedVelocity$handled() {
        return this.signedVelocity$handled;
    }

    @Override
    public void signedVelocity$handled(boolean handled) {
        this.signedVelocity$handled = handled;
    }
}
