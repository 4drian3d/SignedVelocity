package io.github._4drian3d.signedvelocity.paper.utils;

import io.github._4drian3d.signedvelocity.shared.PropertyHolder;
import io.papermc.paper.event.player.ChatEvent;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerChatPreviewEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Stream;

public final class DeprecatedUsageAlert {
  public static final boolean LEGACY_PLUGIN_WARNING = PropertyHolder.readBoolean(
      "io.github._4drian3d.signedvelocity.legacyPluginWarning", true);

  @SuppressWarnings("deprecation")
  public static void blameAboutLegacyPlugins(final Logger logger) {
    Stream.of(
        new LegacyEvent<>(AsyncPlayerChatEvent.class, AsyncPlayerChatEvent.getHandlerList()),
        new LegacyEvent<>(PlayerChatEvent.class, PlayerChatEvent.getHandlerList()),
        new LegacyEvent<>(ChatEvent.class, ChatEvent.getHandlerList()),
        new LegacyEvent<>(AsyncPlayerChatPreviewEvent.class, AsyncPlayerChatPreviewEvent.getHandlerList())
    ).filter(event -> event.listeners.length != 0)
        .forEach(event -> event.printWarning(logger));
  }

  private record LegacyEvent<E extends Event>(Class<E> clazz, RegisteredListener[] listeners) {
    LegacyEvent(Class<E> clazz, HandlerList handlerList) {
      this(clazz, handlerList.getRegisteredListeners());
    }

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
      final StringJoiner builder = new StringJoiner(", ", "", ".");
      final Set<String> legacyPlugins = new ObjectArraySet<>(listeners.length);
      for (final RegisteredListener listener : listeners) {
        final String pluginName = listener.getPlugin().getPluginMeta().getName();
        if (legacyPlugins.add(pluginName)) {
          builder.add(pluginName);
        }
      }
      return builder.toString();
    }
  }
}
