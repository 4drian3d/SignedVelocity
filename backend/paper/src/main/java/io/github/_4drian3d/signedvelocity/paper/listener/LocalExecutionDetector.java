package io.github._4drian3d.signedvelocity.paper.listener;

public interface LocalExecutionDetector {
    boolean CHECK_FOR_LOCAL_CHAT = check();
    private static boolean check() {
        final String property = System.getProperty("io.github._4drian3d.signedvelocity.checkForLocalChat");
        if (property == null) {
            return true;
        } else {
            return Boolean.parseBoolean(property);
        }
    }

    boolean isLocal();
}
