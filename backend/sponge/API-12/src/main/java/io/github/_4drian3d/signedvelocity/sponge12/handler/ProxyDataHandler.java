package io.github._4drian3d.signedvelocity.sponge12.handler;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
import io.github._4drian3d.signedvelocity.shared.types.QueueType;
import io.github._4drian3d.signedvelocity.shared.types.ResultType;
import org.spongepowered.api.network.EngineConnectionState;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;

import java.util.UUID;

public final class ProxyDataHandler implements RawPlayDataHandler<EngineConnectionState.Game> {
  @Inject
  @Named("chat")
  private SignedQueue chatQueue;
  @Inject
  @Named("command")
  private SignedQueue commandQueue;

  @Override
  public void handlePayload(final ChannelBuf data, final EngineConnectionState.Game connection) {
    final UUID playerId = UUID.fromString(data.readUTF());
    final String source = data.readUTF();
    final String result = data.readUTF();

    final SignedQueue queue = switch (QueueType.getOrThrow(source)) {
      case QueueType.COMMAND -> commandQueue;
      case QueueType.CHAT -> chatQueue;
    };
    final SignedResult resulted = switch (ResultType.getOrThrow(result)) {
      case ResultType.CANCEL -> SignedResult.cancel();
      case ResultType.MODIFY -> SignedResult.modify(data.readUTF());
      case ResultType.ALLOWED -> SignedResult.allowed();
    };
    queue.dataFrom(playerId).complete(resulted);
  }
}
