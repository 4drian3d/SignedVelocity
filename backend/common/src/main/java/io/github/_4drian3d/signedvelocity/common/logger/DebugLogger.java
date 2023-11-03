package io.github._4drian3d.signedvelocity.common.logger;

import io.github._4drian3d.signedvelocity.common.PropertyHolder;
import org.slf4j.Logger;

import java.util.function.Supplier;

public interface DebugLogger {
    boolean DEBUG = PropertyHolder.readBoolean("io.github._4drian3d.signedvelocity.debug", false);

    void debugMultiple(Supplier<String[]> supplier);

    void debug(Supplier<String> supplier);

    record Slf4j(Logger logger) implements DebugLogger {

        @Override
        public void debugMultiple(final Supplier<String[]> supplier) {
            if (DEBUG) {
                for (final String line : supplier.get()) {
                    logger.info("[DEBUG] {}", line);
                }
            }
        }

        @Override
        public void debug(Supplier<String> supplier) {
            if (DEBUG) {
                logger.info("[DEBUG] {}", supplier.get());
            }
        }
    }

}
