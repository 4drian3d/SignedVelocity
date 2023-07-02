package io.github._4drian3d.signedvelocity.paper;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SignedQueue {
    private final Map<UUID, QueuedData> signedResults = new ConcurrentHashMap<>();

    public @NotNull QueuedData dataFrom(final UUID uuid) {
        QueuedData data = signedResults.get(uuid);
        if (data == null) {
            data = new QueuedData();
            signedResults.put(uuid, data);
        }
        return data;
    }

    public void removeData(final UUID uuid) {
        this.signedResults.remove(uuid);
    }
}
