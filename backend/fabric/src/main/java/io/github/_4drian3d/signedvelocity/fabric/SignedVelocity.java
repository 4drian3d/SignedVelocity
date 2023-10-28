package io.github._4drian3d.signedvelocity.fabric;

import io.github._4drian3d.signedvelocity.common.SignedQueue;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SignedVelocity implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("SignedVelocity");
    public static final ResourceLocation CHANNEL = new ResourceLocation("signedvelocity", "main");
    public static final SignedQueue CHAT_QUEUE = new SignedQueue();
    public static final SignedQueue COMMAND_QUEUE = new SignedQueue();

    @Override
    public void onInitializeServer() {
        LOGGER.info("Started SignedVelocity");
    }
}