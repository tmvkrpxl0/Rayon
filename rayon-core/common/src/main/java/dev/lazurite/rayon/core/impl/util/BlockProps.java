package dev.lazurite.rayon.core.impl.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockProps {
    public record BlockProperties(float friction, float restitution, boolean collidable) { }

    private static final Map<ResourceLocation, BlockProperties> blockProps = new HashMap<>();

    public static Map<ResourceLocation, BlockProperties> get() {
        return blockProps;
    }

    public static Optional<BlockProperties> get(ResourceLocation identifier) {
        return Optional.ofNullable(blockProps.get(identifier));
    }

    @ExpectPlatform
    public static void load() {
        throw new AssertionError();
    }

}
