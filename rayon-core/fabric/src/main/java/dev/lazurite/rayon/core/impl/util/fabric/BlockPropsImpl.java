package dev.lazurite.rayon.core.impl.util.fabric;

import dev.lazurite.rayon.core.impl.util.BlockProps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

public class BlockPropsImpl {
    public static void load() {
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            final var modid = mod.getMetadata().getId();
            final var rayon = mod.getMetadata().getCustomValue("rayon");

            if (rayon != null) {
                final var blocks = rayon.getAsObject().get("blocks");

                if (blocks != null) {
                    blocks.getAsArray().forEach(block -> {
                        final var name = block.getAsObject().get("name");

                        if (name != null) {
                            final var friction = block.getAsObject().get("friction");
                            final var restitution = block.getAsObject().get("restitution");
                            final var collidable = block.getAsObject().get("collidable");
                            BlockProps.get().put(new ResourceLocation(modid, name.getAsString()), new BlockProps.BlockProperties(
                                    friction == null ? -1.0f : (float) (double) friction.getAsNumber(),
                                    restitution == null ? -1.0f : (float) (double) restitution.getAsNumber(),
                                    collidable == null || collidable.getAsBoolean()
                            ));
                        }
                    });
                }
            }
        });
    }
}
