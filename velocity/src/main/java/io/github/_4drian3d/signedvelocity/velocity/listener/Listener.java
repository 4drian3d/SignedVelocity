package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Injector;
import com.velocitypowered.api.event.AwaitingEventExecutor;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github._4drian3d.signedvelocity.shared.PropertyHolder;
import io.github._4drian3d.signedvelocity.shared.types.QueueType;
import io.github._4drian3d.signedvelocity.shared.types.ResultType;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface Listener<E> extends AwaitingEventExecutor<E> permits PlayerChatListener, PlayerCommandListener, PluginMessageListener, PostPlayerCommandListener {
  void register();

  static void register(final Injector injector) {
    final Listener<?>[] listeners = {
        injector.getInstance(PlayerChatListener.class),
        injector.getInstance(PlayerCommandListener.class),
        injector.getInstance(PostPlayerCommandListener.class),
        injector.getInstance(PluginMessageListener.class)
    };
    for (final Listener<?> listener : listeners) {
      listener.register();
    }
  }

  default void sendAllowedData(final Player player, final ServerConnection connection, final QueueType queueType) {
    connection.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
      output.writeUTF(player.getUniqueId().toString());
      output.writeUTF(queueType.value());
      output.writeUTF(ResultType.ALLOWED.value());
    });
  }

  default void sendCancelData(final Player player, final ServerConnection connection, final QueueType queueType) {
    connection.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
      output.writeUTF(player.getUniqueId().toString());
      output.writeUTF(queueType.value());
      output.writeUTF(ResultType.CANCEL.value());
    });
  }

  default void sendModifiedData(final Player player, final ServerConnection connection, final QueueType queueType, final String modifiedString) {
    connection.sendPluginMessage(SignedVelocity.SIGNEDVELOCITY_CHANNEL, output -> {
      output.writeUTF(player.getUniqueId().toString());
      output.writeUTF(queueType.value());
      output.writeUTF(ResultType.MODIFY.value());
      output.writeUTF(modifiedString);
    });
  }

  int WAIT_FOR_QUEUE_PROCESSING = PropertyHolder.readInt("io.github._4drian3d.signedvelocity.waitForQueueProcessing", 2);

  // Wait for the queue to process the data
  // yeah, I know, this is an ugly/hacky solution, but...
  default void waitForQueueProcessing() {
    if (WAIT_FOR_QUEUE_PROCESSING <= 0) {
      return;
    }
    try {
      Thread.sleep(WAIT_FOR_QUEUE_PROCESSING); // Wait for the queue to process the data
    } catch (InterruptedException _) {
    }
  }
}
