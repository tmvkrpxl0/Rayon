package dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.level;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is a {@link LevelSupplier} which returns a single
 * {@link ClientLevel} object in a {@link List} object.
 */
@Environment(EnvType.CLIENT)
public record ClientLevelSupplier(Minecraft minecraft) implements LevelSupplier {
    @Override
    public List<Level> getAll() {
        final var out = new ArrayList<Level>();

        if (minecraft.level != null) {
            out.add(minecraft.level);
        }

        return out;
    }

    @Override
    public Level get(ResourceKey<Level> key) {
        if (minecraft.level != null && minecraft.level.dimension().equals(key)) {
            return minecraft.level;
        }

        return null;
    }

    @Override
    public Optional<Level> getOptional(ResourceKey<Level> key) {
        return Optional.ofNullable(get(key));
    }
}
