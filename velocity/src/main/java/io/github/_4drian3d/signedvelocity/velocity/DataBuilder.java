package io.github._4drian3d.signedvelocity.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public final class DataBuilder {
    @SuppressWarnings("UnstableApiUsage")
    private final ByteArrayDataOutput data = ByteStreams.newDataOutput();

    private DataBuilder() {
    }

    public DataBuilder append(String string) {
        data.writeUTF(string);
        return this;
    }

    public static DataBuilder builder() {
        return new DataBuilder();
    }

    public byte[] build() {
        return data.toByteArray();
    }
}
