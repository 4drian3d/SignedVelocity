package io.github._4drian3d.signedvelocity.fabric.mixins.injector;

import io.github._4drian3d.signedvelocity.fabric.model.SignedPlayerChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerChatMessage.class)
public class PlayerChatMixin implements SignedPlayerChatMessage {
    @Unique
    public boolean signedVelocity$handled;

    @Override
    public boolean signedVelocity$handled() {
        return signedVelocity$handled;
    }

    @Override
    public void signedVelocity$handled(boolean handled) {
        signedVelocity$handled = handled;
    }
}
