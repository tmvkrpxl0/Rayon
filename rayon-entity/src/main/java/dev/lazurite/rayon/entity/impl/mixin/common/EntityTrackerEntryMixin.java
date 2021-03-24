package dev.lazurite.rayon.entity.impl.mixin.common;

import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

/**
 * Prevents certain packets from being sent for {@link EntityPhysicsElement}s.
 */
@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Shadow @Final private Entity entity;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 1
            )
    )
    public void rotate(Consumer consumer, Object object) {
        if (!(entity instanceof EntityPhysicsElement)) {
            consumer.accept(object);
        }
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 2
            )
    )
    public void velocity(Consumer consumer, Object object) {
        if (!(entity instanceof EntityPhysicsElement)) {
            consumer.accept(object);
        }
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 3
            )
    )
    public void multiple(Consumer consumer, Object object) {
        if (!(entity instanceof EntityPhysicsElement)) {
            consumer.accept(object);
        }
    }
}
