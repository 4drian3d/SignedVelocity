package io.github._4drian3d.signedvelocity.minestom.listener;

import io.github._4drian3d.signedvelocity.minestom.SignedVelocity;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public final class PlayerChatListener implements Consumer<PlayerChatEvent> {
    private static final Field rawMessage;
    private static final Field formattedMessage;
    private static final Method buildDefaultChatMessage;

    static {
        try {
            rawMessage = PlayerChatEvent.class.getDeclaredField("rawMessage");
            rawMessage.setAccessible(true);

            formattedMessage = PlayerChatEvent.class.getDeclaredField("formattedMessage");
            formattedMessage.setAccessible(true);

            buildDefaultChatMessage = PlayerChatEvent.class.getDeclaredMethod("buildDefaultChatMessage");
            buildDefaultChatMessage.setAccessible(true);
        } catch (NoSuchFieldException | NoSuchMethodException e) {
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
                            // set new raw message
                            try {
                                rawMessage.set(event, modifiedChat);
                                Object newFormatted = buildDefaultChatMessage.invoke(event);
                                formattedMessage.set(event, newFormatted);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                MinecraftServer.getExceptionManager().handleException(e);
                            }
                        }
                    }
                }).join();
    }
}
