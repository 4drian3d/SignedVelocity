package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public interface EventListener<E extends Event> extends Listener, EventExecutor {
    @NotNull EventPriority priority();

    boolean ignoreCancelled();

    void handle(final @NotNull E event);

    @NotNull Class<E> eventClass();

    @SuppressWarnings("unchecked")
    @Override
    default void execute(final @NotNull Listener listener, final @NotNull Event event) {
        this.handle((E)event);
    }

    static void registerAll(final SignedVelocity plugin) {
      final PluginManager pluginManager = plugin.getServer().getPluginManager();
      final EventListener<?>[] listeners = {
          new DecorateChatListener(plugin),
          new PlayerChatListener(plugin),
          new PlayerCommandListener(plugin),
          new PlayerQuitListener(plugin)
      };
      for (final EventListener<?> listener : listeners) {
        pluginManager.registerEvent(
            listener.eventClass(),
            listener,
            listener.priority(),
            listener,
            plugin,
            listener.ignoreCancelled()
        );
      }
    }
}
