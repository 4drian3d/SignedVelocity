package io.github._4drian3d.signedvelocity.common.queue;

import org.jetbrains.annotations.Nullable;

public record SignedResult(String message) {
    private static final SignedResult CANCEL = new SignedResult(null);
    private static final SignedResult ALLOWED = new SignedResult(null);

    public boolean cancelled() {
        return this == CANCEL;
    }

    public @Nullable String toModify() {
        return this.message;
    }

    public static SignedResult cancel() {
        return CANCEL;
    }

    public static SignedResult allowed() {
        return ALLOWED;
    }

    public static SignedResult modify(final String message) {
        return new SignedResult(message);
    }
}