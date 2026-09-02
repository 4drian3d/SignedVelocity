package io.github._4drian3d.signedvelocity.fabric.mixins;

import io.github._4drian3d.signedvelocity.fabric.SignedVelocity;
import io.github._4drian3d.signedvelocity.fabric.mixins.accessor.ServerCommonPacketListenerImplAccessor;
import io.github._4drian3d.signedvelocity.fabric.model.SignedChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandSignedPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1)
public abstract class ServerGamePacketListenerMixin {
  @Shadow
  public ServerPlayer player;

  @Shadow
  public abstract void handleChatCommand(ServerboundChatCommandPacket packet);

  @Shadow
  public abstract void handleSignedChatCommand(ServerboundChatCommandSignedPacket packet);

  @SuppressWarnings({"DataFlowIssue", "UnreachableCode", "resource"})
  @Inject(
      method = "handleChatCommand",
      at = @At("HEAD"),
      cancellable = true)
  public void signedVelocity$handleChatCommand(
      ServerboundChatCommandPacket packet, CallbackInfo ci
  ) {
    if (((SignedChatCommandPacket) (Object) packet).signedVelocity$handled()) {
      return;
    }
    ((SignedChatCommandPacket) (Object) packet).signedVelocity$handled(true);
    ci.cancel();

    SignedVelocity.COMMAND_QUEUE.dataFrom(player.getUUID())
        .nextResult()
        .thenAccept(result ->
          ((ServerCommonPacketListenerImplAccessor) (Object) this).signedVelocity$getServer()
              .execute(() -> {
                if (result.cancelled()) {
                  return;
                }
                final String modified = result.toModify();
                if (modified == null) {
                  this.handleChatCommand(packet);
                  return;
                }
                final ServerboundChatCommandPacket modifiedPacket = new ServerboundChatCommandPacket(modified);
                ((SignedChatCommandPacket) (Object) modifiedPacket).signedVelocity$handled(true);
                this.handleChatCommand(modifiedPacket);
              })
        );
  }

  @SuppressWarnings({"DataFlowIssue", "UnreachableCode", "resource"})
  @Inject(
      method = "handleSignedChatCommand",
      at = @At("HEAD"),
      cancellable = true
  )
  public void signedVelocity$handleSignedChatCommand(
      ServerboundChatCommandSignedPacket packet,
      CallbackInfo ci
  ) {
    if (((SignedChatCommandPacket) (Object) packet).signedVelocity$handled()) {
      return;
    }
    ((SignedChatCommandPacket) (Object) packet).signedVelocity$handled(true);
    ci.cancel();

    SignedVelocity.COMMAND_QUEUE.dataFrom(player.getUUID())
        .nextResult()
        .thenAccept(result -> {
          ((ServerCommonPacketListenerImplAccessor) (Object) this).signedVelocity$getServer()
              .execute(() -> {
                if (result.cancelled()) {
                  // TODO: this will cancel the command execution,
                  //  but in the next chain interaction, the server will kick the player
                  return;
                }
                final String modified = result.toModify();
                if (modified == null) {
                  // Allowed chat
                  this.handleSignedChatCommand(packet);
                  return;
                }
                // TODO: the signed chat command modification will break the chain
                final ServerboundChatCommandSignedPacket modifiedPacket = new ServerboundChatCommandSignedPacket(
                    modified,
                    packet.timeStamp(),
                    packet.salt(),
                    packet.argumentSignatures(),
                    packet.lastSeenMessages()
                );
                ((SignedChatCommandPacket) (Object) modifiedPacket).signedVelocity$handled(true);
                this.handleSignedChatCommand(modifiedPacket);
              });
        });
  }
}
