package io.github._4drian3d.signedvelocity.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class QueuedData {
    private static final int timeout = Integer.getInteger("io.github._4drian3d.signedvelocity.timeout", 135);
    private volatile CompletableFuture<SignedResult> futureResult;

    // first
    public void complete(final SignedResult result) {
        if (this.futureResult != null) {
            // UnSynchronized
            if (!this.futureResult.isDone()) {
                this.futureResult.complete(result);
                this.futureResult = null;
            }
        } else {
            // Synchronized
            this.futureResult = CompletableFuture.completedFuture(result);
        }
    }

    //second
    public CompletableFuture<SignedResult> nextResult() {
        if (this.futureResult == null) {
            // UnSynchronized
            return (futureResult = new CompletableFuture<>())
                    .completeOnTimeout(SignedResult.allowed(), timeout, TimeUnit.MILLISECONDS);
        } else {
            // Synchronized
            final CompletableFuture<SignedResult> actual = this.futureResult;
            this.futureResult = null;
            return actual.completeOnTimeout(SignedResult.allowed(), timeout, TimeUnit.MILLISECONDS);
        }
    }

    public CompletableFuture<SignedResult> nextResultWithoutAdvance() {
        if (this.futureResult == null) {
            // UnSynchronized
            return (futureResult = new CompletableFuture<>())
                    .completeOnTimeout(SignedResult.allowed(), timeout, TimeUnit.MILLISECONDS);
        } else {
            // Synchronized
            return this.futureResult.completeOnTimeout(SignedResult.allowed(), timeout, TimeUnit.MILLISECONDS);
        }
    }
}
