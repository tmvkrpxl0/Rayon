package dev.lazurite.rayon.entity.impl.event;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.core.api.event.collision.PhysicsSpaceEvents;
import dev.lazurite.rayon.core.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import dev.lazurite.rayon.entity.impl.collision.space.generator.EntityCollisionGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class ServerEventHandler {
    public static void register(){
        PhysicsSpaceEvents.ELEMENT_ADDED.register(ServerEventHandler::onAddedToSpace);
        registerSpecific();
    }

    @ExpectPlatform
    @Deprecated
    public static void registerSpecific() {
        throw new AssertionError();
        //ServerPlayNetworking.registerGlobalReceiver(RayonEntity.MOVEMENT_PACKET, ServerEventHandler::onMovement);
        ServerEntityEvents.ENTITY_LOAD.register(ServerEventHandler::onEntityLoad);
        EntityTrackingEvents.START_TRACKING.register(ServerEventHandler::onStartTracking);
        EntityTrackingEvents.STOP_TRACKING.register(ServerEventHandler::onStopTracking);
        ServerTickEvents.START_WORLD_TICK.register(ServerEventHandler::onStartLevelTick);
    }

    public static void onAddedToSpace(MinecraftSpace space, ElementRigidBody rigidBody) {
        if (rigidBody instanceof EntityRigidBody entityBody) {
            final var pos = entityBody.getElement().asEntity().position();
            final var box = entityBody.getElement().asEntity().getBoundingBox();
            entityBody.setPhysicsLocation(Convert.toBullet(pos.add(0, box.getYsize() / 2.0, 0)));
        }
    }

    public static void onEntityLoad(Entity entity, ServerLevel level) {
        if (entity instanceof EntityPhysicsElement element && !RayonEntity.getTrackingPlayers(entity.getId()).isEmpty()) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    public static void onStartTracking(Entity entity, ServerPlayer player) {
        if (entity instanceof EntityPhysicsElement element) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.addCollisionObject(element.getRigidBody()));
        }
    }

    public static void onStopTracking(Entity entity, ServerPlayer player) {
        if (entity instanceof EntityPhysicsElement element && RayonEntity.getTrackingPlayers(entity.getId()).isEmpty()) {
            final var space = MinecraftSpace.get(entity.level);
            space.getWorkerThread().execute(() -> space.removeCollisionObject(element.getRigidBody()));
        }
    }

    public static void onStartLevelTick(ServerLevel level) {
        final var space = MinecraftSpace.get(level);
        EntityCollisionGenerator.applyEntityCollisions(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            if (rigidBody.isActive()) {
                /* Movement */
                if (rigidBody.isPositionDirty() && rigidBody.getPriorityPlayer() == null) {
                    rigidBody.sendMovementPacket();
                }

                /* Properties */
                if (rigidBody.arePropertiesDirty()) {
                    rigidBody.sendPropertiesPacket();
                }
            }

            /* Set entity position */
            final var entity = rigidBody.getElement().asEntity();
            final var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            final var offset = rigidBody.boundingBox(new BoundingBox()).getYExtent();
            entity.absMoveTo(location.x, location.y - offset, location.z);
        }
    }
}