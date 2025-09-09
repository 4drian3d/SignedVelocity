package io.github._4drian3d.signedvelocity.minestom.listener;

import io.github._4drian3d.signedvelocity.minestom.SignedVelocity;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.function.Consumer;

public final class PlayerChatListener implements Consumer<PlayerChatEvent> {
    private static final VarHandle rawMessage;
    private static final MethodHandle buildDefaultChatMessage;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(PlayerChatEvent.class, MethodHandles.lookup());
            rawMessage = lookup.findVarHandle(PlayerChatEvent.class, "rawMessage", String.class);

            final MethodType chatMessageMethodType = MethodType.methodType(Component.class);
            buildDefaultChatMessage = lookup.findVirtual(PlayerChatEvent.class, "buildDefaultChatMessage", chatMessageMethodType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void accept(final PlayerChatEvent event) {
        final Player player = event.getPlayer();
        SignedVelocity.chatQueue().dataFrom(player.getUuid())
                .nextResult()
                .thenAccept(result -> {
                    if (result.cancelled()) {
                        event.setCancelled(true);
                    } else {
                        final String modifiedChat = result.toModify();
                        if (modifiedChat != null) {
                            try {
                                rawMessage.set(event, modifiedChat);
                                event.setFormattedMessage((Component) buildDefaultChatMessage.invoke(event));
                            } catch (Throwable e) {
                                MinecraftServer.getExceptionManager().handleException(e);
                            }
                        }
                    }
                }).join();
    }
}
