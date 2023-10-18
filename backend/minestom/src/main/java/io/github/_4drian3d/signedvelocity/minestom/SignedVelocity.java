package io.github._4drian3d.signedvelocity.minestom;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import io.github._4drian3d.signedvelocity.minestom.listener.PlayerChatListener;
import io.github._4drian3d.signedvelocity.minestom.listener.PlayerCommandListener;
import io.github._4drian3d.signedvelocity.minestom.listener.PluginMessageListener;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerCommandEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerPluginMessageEvent;
import net.minestom.server.extensions.Extension;

public final class SignedVelocity extends Extension {
    public static final String CHANNEL = "signedvelocity:main";
    private final SignedQueue chatQueue = new SignedQueue();
    private final SignedQueue commandQueue = new SignedQueue();

    @Override
    public void initialize() {
        getEventNode().addListener(PlayerChatEvent.class, new PlayerChatListener(this));
        getEventNode().addListener(PlayerCommandEvent.class, new PlayerCommandListener(this));
        getEventNode().addListener(PlayerPluginMessageEvent.class, new PluginMessageListener(this));
        getEventNode().addListener(PlayerDisconnectEvent.class, event -> {
            final var uuid = event.getPlayer().getUuid();
            this.commandQueue.removeData(uuid);
            this.chatQueue.removeData(uuid);
        });
    }

    @Override
    public void terminate() {
    }

    public SignedQueue chatQueue() {
        return chatQueue;
    }

    public SignedQueue commandQueue() {
        return commandQueue;
    }
}
