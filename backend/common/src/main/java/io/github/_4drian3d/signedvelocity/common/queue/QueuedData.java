package io.github._4drian3d.signedvelocity.common.queue;

import io.github._4drian3d.signedvelocity.common.PropertyHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public final class QueuedData {
    private static final int timeout = PropertyHolder.readInt("io.github._4drian3d.signedvelocity.timeout", 50);
    private final Queue<SignedResult> results = new ConcurrentLinkedQueue<>();
    private final Queue<CompletableFuture<SignedResult>> unSyncronizedQueue = new ConcurrentLinkedQueue<>();

    public void complete(final SignedResult result) {
        this.results.add(result);
        final var unSynchronized = unSyncronizedQueue.poll();
        if (unSynchronized != null) {
            unSynchronized.complete(result);
        }
    }

    public CompletableFuture<SignedResult> nextResult() {
        final SignedResult result = results.poll();
        return futureFrom(result);
    }

    public CompletableFuture<SignedResult> nextResultWithoutAdvance() {
        final SignedResult result = results.peek();
        return futureFrom(result);
    }

    private CompletableFuture<SignedResult> futureFrom(final @Nullable SignedResult result) {
        if (result == null) {
            final CompletableFuture<SignedResult> future = new CompletableFuture<>();
            future.completeOnTimeout(SignedResult.allowed(), timeout, TimeUnit.MILLISECONDS);
            unSyncronizedQueue.add(future);
            return future;
        } else {
            return CompletableFuture.completedFuture(result);
        }
    }
}
