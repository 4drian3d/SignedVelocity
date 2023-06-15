package io.github._4drian3d.signedvelocity.paper.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.github._4drian3d.signedvelocity.paper.SignedQueue;
import io.github._4drian3d.signedvelocity.paper.SignedVelocity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PluginMessagingListener implements PluginMessageListener {
    private final SignedVelocity plugin;

    public PluginMessagingListener(SignedVelocity plugin) {
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
        final ByteArrayDataInput input = ByteStreams.newDataInput(message);
        final String source = input.readUTF();
        final String result = input.readUTF();
        final String username = input.readUTF();

        final SignedQueue queue = Objects.equals(source, "COMMAND_RESULT")
                ? plugin.getCommandQueue()
                : plugin.getChatQueue();
        final SignedQueue.SignedResult resulted = Objects.equals(result, "CANCEL")
                ? SignedQueue.SignedResult.cancel()
                : SignedQueue.SignedResult.modify(input.readUTF());
        final Player messagePlayer = plugin.getServer().getPlayer(username);
        queue.queueResult(messagePlayer, resulted);
    }
}
