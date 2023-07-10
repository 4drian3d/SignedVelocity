package io.github._4drian3d.signedvelocity.paper;

import org.checkerframework.checker.nullness.qual.Nullable;

public record SignedResult(@Nullable String message) {
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