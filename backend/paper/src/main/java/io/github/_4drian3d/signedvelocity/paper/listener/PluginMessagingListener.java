package io.github._4drian3d.signedvelocity.paper.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.shared.SignedConstants;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import io.github._4drian3d.signedvelocity.shared.types.QueueType;
import io.github._4drian3d.signedvelocity.shared.types.ResultType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public final class PluginMessagingListener implements PluginMessageListener {
  private final SignedVelocity plugin;

  public PluginMessagingListener(final SignedVelocity plugin) {
    this.plugin = plugin;
  }

  @Override
  public void onPluginMessageReceived(
      @NotNull final String channel,
      @NotNull final Player player,
      final byte @NotNull [] message
  ) {
    if (!Objects.equals(channel, SignedConstants.SIGNED_PLUGIN_CHANNEL)) {
      return;
    }
    plugin.debugLogger().debug(() -> "[Plugin Message] Received on: " + System.currentTimeMillis());
    final ByteArrayDataInput input = ByteStreams.newDataInput(message);

    final UUID playerId = UUID.fromString(input.readUTF());
    final String source = input.readUTF();
    final String result = input.readUTF();

    final SignedQueue queue = switch (QueueType.getOrThrow(source)) {
      case QueueType.COMMAND -> plugin.getCommandQueue();
      case QueueType.CHAT -> plugin.getChatQueue();
    };
    final SignedResult resulted = switch (ResultType.getOrThrow(result)) {
      case ResultType.CANCEL -> SignedResult.cancel();
      case ResultType.MODIFY -> SignedResult.modify(input.readUTF());
      case ResultType.ALLOWED -> SignedResult.allowed();
    };

    queue.dataFrom(playerId).complete(resulted);
    plugin.debugLogger().debugMultiple(() -> new String[]{
        "[Plugin Message] Received Valid Message",
        "| Queue: " + source,
        "| Result: " + result,
        "| Message: " + resulted.message()
    });
  }
}
