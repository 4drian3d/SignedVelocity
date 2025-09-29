package io.github._4drian3d.signedvelocity.paper.listener;

import io.github._4drian3d.signedvelocity.shared.PropertyHolder;

public interface LocalExecutionDetector {
    boolean CHECK_FOR_LOCAL_CHAT = PropertyHolder.readBoolean("io.github._4drian3d.signedvelocity.checkForLocalChat", true);
    StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    boolean isLocal();
}
