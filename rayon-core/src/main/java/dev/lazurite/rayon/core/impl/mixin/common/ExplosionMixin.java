package dev.lazurite.rayon.core.impl.mixin.common;

import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

/**
 * Allows {@link PhysicsElement} objects to be affected by explosions.
 */
@Mixin(Explosion.class)
public class ExplosionMixin {
    @Unique private Entity entity;

    @Inject(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void collectBlocksAndDamageEntities(CallbackInfo info, Set set, float q, int r, int s, int t, int u, int v, int w, List list, Vec3 vec3, int x, Entity entity) {
        this.entity = entity;
    }

    @ModifyArg(
            method = "explode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
            )
    )
    public Vec3 setVelocity(Vec3 velocity) {
        if (entity instanceof PhysicsElement) {
            var rigidBody = ((PhysicsElement) entity).getRigidBody();

            MinecraftSpace.get(entity.getLevel()).getWorkerThread().execute(() ->
                rigidBody.applyCentralImpulse(Convert.toBullet(velocity).multLocal(rigidBody.getMass()).multLocal(100))
            );
        }

        return velocity;
    }
}
