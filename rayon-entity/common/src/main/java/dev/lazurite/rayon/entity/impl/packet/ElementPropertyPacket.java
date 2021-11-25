package dev.lazurite.rayon.entity.impl.packet;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class ElementPropertyPacket {
    private final int entityId;
    private final float mass;
    private final float dragCoefficient;
    private final float friction;
    private final float restitution;
    private final boolean doTerrainLoading;
    private final UUID priorityPlayer;

    public ElementPropertyPacket(
            int entityId,
            float mass,
            float dragCoefficient,
            float friction,
            float restitution,
            boolean doTerrainLoading,
            UUID priorityPlayer
    ) {
        this.entityId = entityId;
        this.mass = mass;
        this.dragCoefficient = dragCoefficient;
        this.friction = friction;
        this.restitution = restitution;
        this.doTerrainLoading = doTerrainLoading;
        this.priorityPlayer = priorityPlayer;
    }

    public ElementPropertyPacket(EntityRigidBody body) {
        this(
                body.getElement().asEntity().getId(),
                body.getMass(),
                body.getDragCoefficient(),
                body.getFriction(),
                body.getRestitution(),
                body.shouldDoTerrainLoading(),
                body.getPriorityPlayer() == null ? new UUID(0, 0) : body.getPriorityPlayer().getUUID()
        );
    }

    @ExpectPlatform
    public static void send(ElementPropertyPacket packet){
        throw new AssertionError();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(mass);
        buf.writeFloat(dragCoefficient);
        buf.writeFloat(friction);
        buf.writeFloat(restitution);
        buf.writeBoolean(doTerrainLoading);
        buf.writeUUID(priorityPlayer);
    }

    public static ElementPropertyPacket decode(FriendlyByteBuf buf) {
        final var entityId = buf.readInt();
        final var mass = buf.readFloat();
        final var dragCoefficient = buf.readFloat();
        final var friction = buf.readFloat();
        final var restitution = buf.readFloat();
        final var doTerrainLoading = buf.readBoolean();
        final var priorityPlayer = buf.readUUID();

        return new ElementPropertyPacket(entityId, mass, dragCoefficient, friction, restitution, doTerrainLoading, priorityPlayer);
    }

    public static void accept(ElementPropertyPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        PhysicsThread.getOptional(minecraft).ifPresent(thread -> thread.execute(() -> {
            if (minecraft.player != null) {
                final var level = minecraft.player.clientLevel;
                final var entity = level.getEntity(packet.entityId);
                if (entity instanceof EntityPhysicsElement element) {
                    final var rigidBody = element.getRigidBody();
                    rigidBody.setMass(packet.mass);
                    rigidBody.setDragCoefficient(packet.dragCoefficient);
                    rigidBody.setFriction(packet.friction);
                    rigidBody.setRestitution(packet.restitution);
                    rigidBody.setDoTerrainLoading(packet.doTerrainLoading);
                    rigidBody.prioritize(rigidBody.getSpace().getLevel().getPlayerByUUID(packet.priorityPlayer));
                }
            }
        }));
    }
}
