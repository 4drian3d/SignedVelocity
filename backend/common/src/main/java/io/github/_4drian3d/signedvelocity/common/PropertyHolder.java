package io.github._4drian3d.signedvelocity.common;

public final class PropertyHolder {
    private PropertyHolder() {
        throw new AssertionError();
    }

    public static boolean readBoolean(final String property, final boolean defaultValue) {
        final String value = System.getProperty(property);
        if (value == null) {
            return defaultValue;
        } else {
            return "true".equalsIgnoreCase(value);
        }
    }

    public static String readValue(final String property, final String defaultValue) {
        return System.getProperty(property, defaultValue);
    }

    public static int readInt(final String property, final int defaultValue) {
        final String value = System.getProperty(property);
        if (value == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (final Exception ignored) {
                return defaultValue;
            }
        }
    }
}
