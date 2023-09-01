package io.github._4drian3d.signedvelocity.paper.listener;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
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
}
