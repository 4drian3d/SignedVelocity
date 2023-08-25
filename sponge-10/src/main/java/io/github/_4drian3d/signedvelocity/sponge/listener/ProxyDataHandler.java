package io.github._4drian3d.signedvelocity.sponge.listener;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.common.SignedResult;
import org.spongepowered.api.network.EngineConnection;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;

import java.util.UUID;

public final class ProxyDataHandler implements RawPlayDataHandler<EngineConnection> {
    @Inject
    @Named("chat")
    private SignedQueue chatQueue;
    @Inject
    @Named("command")
    private SignedQueue commandQueue;

    @Override
    public void handlePayload(final ChannelBuf data, final EngineConnection connection) {
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
