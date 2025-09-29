package io.github._4drian3d.signedvelocity.minestom.listener;

import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.shared.SignedConstants;
import io.github._4drian3d.signedvelocity.minestom.SignedVelocity;
import io.github._4drian3d.signedvelocity.shared.types.QueueType;
import io.github._4drian3d.signedvelocity.shared.types.ResultType;
import net.minestom.server.event.player.PlayerPluginMessageEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class PluginMessageListener implements Consumer<PlayerPluginMessageEvent> {

  @Override
  public void accept(PlayerPluginMessageEvent event) {
    if (!Objects.equals(event.getIdentifier(), SignedConstants.SIGNED_PLUGIN_CHANNEL)) {
      return;
    }
    try (final DataInput input = new DataInput(event.getMessage())) {
      final UUID playerId = UUID.fromString(input.readUTF());
      final String source = input.readUTF();
      final String result = input.readUTF();

      final SignedQueue queue = switch (QueueType.getOrThrow(source)) {
        case QueueType.COMMAND -> SignedVelocity.commandQueue();
        case QueueType.CHAT -> SignedVelocity.chatQueue();
      };
      final SignedResult resulted = switch (ResultType.getOrThrow(result)) {
        case ResultType.CANCEL -> SignedResult.cancel();
        case ResultType.MODIFY -> SignedResult.modify(input.readUTF());
        case ResultType.ALLOWED -> SignedResult.allowed();
      };
      queue.dataFrom(playerId).complete(resulted);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private record DataInput(DataInputStream dataStream) implements AutoCloseable {
    DataInput(final byte[] data) {
      this(new DataInputStream(new ByteArrayInputStream(data)));
    }

    public String readUTF() {
      try {
        return dataStream.readUTF();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void close() throws Exception {
      dataStream.close();
    }
  }
}
