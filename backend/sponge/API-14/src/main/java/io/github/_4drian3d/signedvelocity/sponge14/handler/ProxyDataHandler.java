package io.github._4drian3d.signedvelocity.sponge14.handler;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.common.queue.SignedResult;
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

        final SignedQueue queue = switch (source) {
            case "COMMAND_RESULT" -> commandQueue;
            case "CHAT_RESULT" -> chatQueue;
            default -> throw new IllegalArgumentException("Invalid source " + source);
        };
        final SignedResult resulted = switch (result) {
            case "CANCEL" -> SignedResult.cancel();
            case "MODIFY" -> SignedResult.modify(data.readUTF());
            case "ALLOWED" -> SignedResult.allowed();
            default -> throw new IllegalArgumentException("Invalid result " + result);
        };
        queue.dataFrom(playerId).complete(resulted);
    }
}
