package dev.lazurite.rayon.physics.handler;

import com.bulletphysics.collision.shapes.CollisionShape;
import dev.lazurite.rayon.entity.PhysicsEntity;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * The basic PhysicsHandler interface. Includes everything
 * that a PhysicsHandler instance should be able to do.
 * @author Ethan Johnson
 */
public interface PhysicsHandler {
    Vector3f getPosition();
    Vector3f getLinearVelocity();
    Vector3f getAngularVelocity();
    Quat4f getOrientation();
    PhysicsEntity getEntity();

    void setPosition(Vector3f position);
    void setLinearVelocity(Vector3f linearVelocity);
    void setAngularVelocity(Vector3f angularVelocity);
    void setOrientation(Quat4f orientation);
    boolean isActive();

    static PhysicsHandler create(PhysicsEntity physicsEntity, CollisionShape shape) {
        if (physicsEntity.getEntityWorld().isClient()) {
            return new ClientPhysicsHandler(physicsEntity, shape);
        } else {
            return new ServerPhysicsHandler(physicsEntity);
        }
    }
}
