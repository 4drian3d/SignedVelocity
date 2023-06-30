package io.github._4drian3d.signedvelocity.paper.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github._4drian3d.signedvelocity.paper.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class PluginMessagingListener implements PluginMessageListener {
    private final SignedVelocity plugin;

    public PluginMessagingListener(final SignedVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(
            @NotNull final String channel,
            @NotNull final Player player,
            final byte@NotNull[] message
    ) {
        if (!Objects.equals(channel, SignedVelocity.CHANNEL)) {
            return;
        }
        @SuppressWarnings("UnstableApiUsage")
        final ByteArrayDataInput input = ByteStreams.newDataInput(message);
        final String source = input.readUTF();
        final String result = input.readUTF();
        final String username = input.readUTF();

        final SignedQueue queue = switch (source) {
            case "COMMAND_RESULT" -> plugin.getCommandQueue();
            case "CHAT_RESULT" -> plugin.getChatQueue();
            default -> throw new IllegalArgumentException("Invalid source " + source);
        };
        final SignedQueue.SignedResult resulted = switch (result) {
            case "CANCEL" -> SignedQueue.SignedResult.cancel();
            case "MODIFY" -> SignedQueue.SignedResult.modify(input.readUTF());
            default -> throw new IllegalArgumentException("Invalid result " + result);
        };
        final Player messagePlayer = plugin.getServer().getPlayer(UUID.fromString(username));
        queue.queueResult(messagePlayer, resulted);
    }
}
