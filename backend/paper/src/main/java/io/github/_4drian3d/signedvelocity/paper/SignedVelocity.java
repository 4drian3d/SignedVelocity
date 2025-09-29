package io.github._4drian3d.signedvelocity.paper;

import io.github._4drian3d.signedvelocity.shared.logger.DebugLogger;
import io.github._4drian3d.signedvelocity.common.queue.SignedQueue;
import io.github._4drian3d.signedvelocity.shared.SignedConstants;
import io.github._4drian3d.signedvelocity.paper.listener.EventListener;
import io.github._4drian3d.signedvelocity.paper.listener.PluginMessagingListener;
import io.github._4drian3d.signedvelocity.paper.utils.DeprecatedUsageAlert;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignedVelocity extends JavaPlugin {
  private final SignedQueue chatQueue = new SignedQueue();
  private final SignedQueue commandQueue = new SignedQueue();
  private final DebugLogger debugLogger = new DebugLogger.Slf4j(getSLF4JLogger());

  @Override
  public void onEnable() {
    this.getServer()
        .getMessenger()
        .registerIncomingPluginChannel(
            this,
            SignedConstants.SIGNED_PLUGIN_CHANNEL,
            new PluginMessagingListener(this)
        );

    EventListener.registerAll(this);

    if (DeprecatedUsageAlert.LEGACY_PLUGIN_WARNING) {
      DeprecatedUsageAlert.blameAboutLegacyPlugins(this.getSLF4JLogger());
    }
  }

  public SignedQueue getChatQueue() {
    return chatQueue;
  }

  public SignedQueue getCommandQueue() {
    return commandQueue;
  }

  public DebugLogger debugLogger() {
    return this.debugLogger;
  }
}
