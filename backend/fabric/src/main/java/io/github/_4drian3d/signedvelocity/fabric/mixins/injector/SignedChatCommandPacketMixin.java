package io.github._4drian3d.signedvelocity.fabric.mixins.injector;

import io.github._4drian3d.signedvelocity.fabric.model.SignedChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerboundChatCommandSignedPacket.class)
public class SignedChatCommandPacketMixin implements SignedChatCommandPacket {
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
