package dev.lazurite.rayon.entity.common.impl.collision.space.generator;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.entity.EntitySupplier;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.entity.common.impl.collision.body.EntityRigidBody;

public class EntityCollisionGenerator {
    public static void applyEntityCollisions(MinecraftSpace space) {
        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            final var box = rigidBody.boundingBox(new BoundingBox());
            final var location = rigidBody.getPhysicsLocation(new Vector3f()).subtract(new Vector3f(0, -box.getYExtent(), 0));
            final var mass = rigidBody.getMass();

            for (var entity : EntitySupplier.getInsideOf(rigidBody)) {
                var entityPos = Convert.toBullet(entity.position().add(0, entity.getBoundingBox().getYsize(), 0));
                var normal = location.subtract(entityPos).multLocal(new Vector3f(1, 0, 1)).normalize();

                var intersection = entity.getBoundingBox().intersect(Convert.toMinecraft(box));
                var force = normal.clone().multLocal((float) intersection.getSize() / (float) Convert.toMinecraft(box).getSize())
                        .multLocal(mass).multLocal(new Vector3f(1, 0, 1));
                rigidBody.applyCentralImpulse(force);
            }
        }
    }
}
