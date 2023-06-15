package io.github._4drian3d.signedvelocity.paper;

import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SignedQueue {
    private final Map<UUID, SignedResult> signedResults = new ConcurrentHashMap<>();

    public void queueResult(final UUID uuid, final SignedResult next) {
        signedResults.put(uuid, next);
    }

    public void queueResult(final Player player, final SignedResult next) {
        if (player != null) this.queueResult(player.getUniqueId(), next);
    }

    public @Nullable SignedResult nextResult(final UUID uuid) {
        return this.signedResults.remove(uuid);
    }

    public static class SignedResult {
        private static final SignedResult CANCEL = new SignedResult(null);

        private final String message;

        private SignedResult(@Nullable final String message) {
            this.message = message;
        }


        public boolean cancelled() {
            return this.message == null;
        }

        public @Nullable String toModify() {
            return this.message;
        }

        public static SignedResult cancel() {
            return CANCEL;
        }

        public static SignedResult modify(final String message) {
            return new SignedResult(message);
        }
    }
}
