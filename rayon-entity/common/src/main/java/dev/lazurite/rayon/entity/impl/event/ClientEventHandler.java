package dev.lazurite.rayon.entity.impl.event;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.core.impl.bullet.collision.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.bullet.collision.space.supplier.player.ClientPlayerSupplier;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import dev.lazurite.rayon.entity.impl.collision.space.generator.EntityCollisionGenerator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class ClientEventHandler {
    
    @ExpectPlatform
    public static void register() {
        throw new AssertionError();
        /*ClientPlayNetworking.registerGlobalReceiver(RayonEntity.MOVEMENT_PACKET, ClientEventHandler::onMovement);
        ClientPlayNetworking.registerGlobalReceiver(RayonEntity.PROPERTIES_PACKET, ClientEventHandler::onProperties);
        ClientEntityEvents.ENTITY_LOAD.register(ClientEventHandler::onLoad);
        ClientEntityEvents.ENTITY_UNLOAD.register(ClientEventHandler::onUnload);
        ClientTickEvents.START_WORLD_TICK.register(ClientEventHandler::onStartLevelTick);*/
    }

    public static void onLoad(Entity entity, ClientLevel level) {
        if (entity instanceof EntityPhysicsElement element) {
            PhysicsThread.get(level).execute(() ->
                    MinecraftSpace.getOptional(level).ifPresent(space ->
                            space.addCollisionObject(element.getRigidBody())
                    )
            );
        }
    }

    public static void onUnload(Entity entity, ClientLevel level) {
        if (entity instanceof EntityPhysicsElement element) {
            PhysicsThread.get(level).execute(() ->
                MinecraftSpace.getOptional(level).ifPresent(space ->
                        space.removeCollisionObject(element.getRigidBody())
                )
            );
       }
    }

    public static void onStartLevelTick(ClientLevel level) {
        final var space = MinecraftSpace.get(level);
        EntityCollisionGenerator.applyEntityCollisions(space);

        for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
            /* Movement */
            if (rigidBody.isActive() && rigidBody.isPositionDirty() && ClientPlayerSupplier.get().equals(rigidBody.getPriorityPlayer())) {
                rigidBody.sendMovementPacket();
            }

            /* Set entity position */
            final var element = ((EntityPhysicsElement) rigidBody.getElement());
            final var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
            final var offset = rigidBody.boundingBox(new BoundingBox()).getYExtent();
            element.asEntity().absMoveTo(location.x, location.y - offset, location.z);
        }
    }
}
