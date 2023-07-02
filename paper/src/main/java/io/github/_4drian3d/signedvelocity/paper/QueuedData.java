package io.github._4drian3d.signedvelocity.paper;

import java.util.concurrent.CompletableFuture;

public final class QueuedData {
    private volatile CompletableFuture<SignedResult> futureResult;

    // first
    public void complete(final SignedResult result) {
        if (this.futureResult != null) {
            // UnSynchronized
            if (!this.futureResult.isDone()) {
                this.futureResult.complete(result);
            }
            this.futureResult = null;
        } else {
            // Synchronized
            this.futureResult = CompletableFuture.completedFuture(result);
        }
    }

    //second
    public CompletableFuture<SignedResult> nextResult() {
        if (this.futureResult == null) {
            // UnSynchronized
            return futureResult = new CompletableFuture<>();
        } else {
            // Synchronized
            final CompletableFuture<SignedResult> actual = this.futureResult;
            this.futureResult = null;
            return actual;
        }
    }
}
