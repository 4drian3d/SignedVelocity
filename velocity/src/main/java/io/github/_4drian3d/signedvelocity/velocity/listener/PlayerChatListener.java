package io.github._4drian3d.signedvelocity.velocity.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import io.github._4drian3d.signedvelocity.shared.types.QueueType;
import io.github._4drian3d.signedvelocity.velocity.SignedVelocity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

final class PlayerChatListener implements Listener<@NotNull PlayerChatEvent> {
  @Inject
  private EventManager eventManager;
  @Inject
  private SignedVelocity plugin;

  @Override
  public EventTask executeAsync(final PlayerChatEvent event) {
    final PlayerChatEvent.ChatResult result = event.getResult();

    return EventTask.withContinuation(continuation -> {
      final Player player = event.getPlayer();

      // Denied
      // | The player has an old version, so you can safely deny execution from Velocity
      if (!result.isAllowed() && player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) < 0) {
        continuation.resume();
        return;
      }

      final ServerConnection server = player.getCurrentServer().orElseThrow();

      // Allowed
      // | If the message is allowed simply transmit that should be accepted
      if (result == PlayerChatEvent.ChatResult.allowed()) {
        this.sendAllowedData(player, server, QueueType.CHAT);
        continuation.resume();
        return;
      }

      //noinspection deprecation
      event.setResult(PlayerChatEvent.ChatResult.allowed());

      final String finalMessage = result.getMessage().orElse(null);

      // Cancelled
      // | The result is to cancel the execution
      if (finalMessage == null) {
        this.sendCancelData(player, server, QueueType.CHAT);
        continuation.resume();
        return;
      }

      // ALLOWED
      // | If the result of the event is to modify the message,
      // | but the modified message is the same as the executed one, simply accept the execution
      if (Objects.equals(finalMessage, event.getMessage())) {
        this.sendAllowedData(player, server, QueueType.CHAT);
        continuation.resume();
        return;
      }

      // Modified
      // | The result is to modify the command
      this.sendModifiedData(player, server, QueueType.CHAT, finalMessage);

      continuation.resume();
    });
  }

  @Override
  public void register() {
    eventManager.register(plugin, PlayerChatEvent.class, Short.MIN_VALUE, this);
  }
}