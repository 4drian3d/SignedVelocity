package io.github._4drian3d.signedvelocity.paper;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.listener.*;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class SignedVelocity extends JavaPlugin {
    public static final String CHANNEL = "signedvelocity:main";
    private final SignedQueue chatQueue = new SignedQueue();
    private final SignedQueue commandQueue = new SignedQueue();

    @Override
    public void onEnable() {
        final Server server = getServer();
        server.getMessenger().registerIncomingPluginChannel(this, CHANNEL, new PluginMessagingListener(this));

        final PluginManager pluginManager = server.getPluginManager();
        Stream.of(
                new DecorateChatListener(this),
                new PlayerChatListener(this),
                new PlayerCommandListener(this),
                new PlayerQuitListener(this)
        ).forEach(listener -> pluginManager.registerEvent(
                listener.eventClass(),
                listener,
                listener.priority(),
                listener,
                this,
                listener.ignoreCancelled()
        ));
        this.blameAboutLegacyPlugins();
    }

    public SignedQueue getChatQueue() {
        return chatQueue;
    }

    public SignedQueue getCommandQueue() {
        return commandQueue;
    }

    @SuppressWarnings("deprecation")
    private void blameAboutLegacyPlugins() {
        final Logger logger = this.getSLF4JLogger();
        Stream.of(
                new LegacyEvent<>(org.bukkit.event.player.AsyncPlayerChatEvent.class, org.bukkit.event.player.AsyncPlayerChatEvent.getHandlerList()),
                new LegacyEvent<>(org.bukkit.event.player.PlayerChatEvent.class, org.bukkit.event.player.PlayerChatEvent.getHandlerList()),
                new LegacyEvent<>(io.papermc.paper.event.player.ChatEvent.class, io.papermc.paper.event.player.ChatEvent.getHandlerList()),
                new LegacyEvent<>(org.bukkit.event.player.AsyncPlayerChatPreviewEvent.class, org.bukkit.event.player.AsyncPlayerChatPreviewEvent.getHandlerList())
        ).filter(event -> event.listeners.length != 0).forEach(event -> event.printWarning(logger));
    }

    private record LegacyEvent<E extends Event>(Class<E> clazz, RegisteredListener[] listeners) {
        LegacyEvent(Class<E> clazz, HandlerList handlerList) {
            this(clazz, handlerList.getRegisteredListeners());
        }

        @SuppressWarnings("UnstableApiUsage")
        private void printWarning(final Logger logger) {
            logger.warn("------------------------------");
            final String legacyPluginList = legacyPluginList();
            logger.warn("The following plugins have listener in legacy {} event: {}", clazz.getName(), legacyPluginList);
            logger.warn("This may negatively affect the functionality of SignedVelocity,");
            logger.warn("please report to the author to use Paper's AsyncChatEvent and/or AsyncChatDecorateEvent.");
            logger.warn("------------------------------");
        }

        @NotNull
        private String legacyPluginList() {
            final StringBuilder builder = new StringBuilder();
            final Set<String> legacyPlugins = new HashSet<>();
            for (int i = 0; i < listeners.length; i++) {
                final RegisteredListener listener = listeners[i];
                final String pluginName = listener.getPlugin().getPluginMeta().getName();
                if (legacyPlugins.add(pluginName)) {
                    builder.append(pluginName);
                }
                if (i + 1 != listeners.length) {
                    builder.append(", ");
                }
            }
            builder.append('.');
            return builder.toString();
        }
    }
}
