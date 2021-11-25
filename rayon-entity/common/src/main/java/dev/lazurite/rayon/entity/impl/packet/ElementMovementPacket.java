package dev.lazurite.rayon.entity.impl.packet;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.core.impl.bullet.math.Convert;
import dev.lazurite.rayon.core.impl.bullet.thread.PhysicsThread;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import dev.lazurite.rayon.entity.impl.collision.body.EntityRigidBody;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import dev.lazurite.toolbox.api.math.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;

public class ElementMovementPacket {
    public final int entityId;
    private final Quaternion rotation;
    private final Vector3f location;
    private final Vector3f linearVelocity;
    private final Vector3f angularVelocity;

    public ElementMovementPacket(int entityId, Quaternion rotation, Vector3f location, Vector3f linearVelocity, Vector3f angularVelocity) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.location = location;
        this.linearVelocity = linearVelocity;
        this.angularVelocity = angularVelocity;
    }

    public ElementMovementPacket(EntityRigidBody body) {
        this.entityId = body.getElement().asEntity().getId();
        this.rotation = body.getPhysicsRotation(new Quaternion());
        this.location = body.getPhysicsLocation(new Vector3f());
        this.linearVelocity = body.getLinearVelocity(new Vector3f());
        this.angularVelocity = body.getAngularVelocity(new Vector3f());
    }

    @ExpectPlatform
    public static void send(ElementMovementPacket packet, boolean isToServer, @Nullable Player priority) {
        throw new AssertionError();
    }

    public static ElementMovementPacket decode(FriendlyByteBuf buf) {
        final var entityId = buf.readInt();
        final var rotation = Convert.toBullet(QuaternionHelper.fromBuffer(buf));
        final var location = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var linearVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        final var angularVelocity = Convert.toBullet(VectorHelper.fromBuffer(buf));
        return new ElementMovementPacket(entityId, rotation, location, linearVelocity, angularVelocity);
    }

    /**
     * @param player This is also used to determine direction, something like [{@code boolean isToServer}]
     *               can be used instead
     */
    public static void accept(ElementMovementPacket packet, Executor executor, Player player) {
        executor.execute(() -> {
            if (player instanceof ServerPlayer serverPlayer) {
                PhysicsThread.getOptional(serverPlayer.getServer()).ifPresent(thread -> thread.execute(() -> {
                    final var level = serverPlayer.getLevel();
                    final var entity = level.getEntity(packet.entityId);

                    if (entity instanceof EntityPhysicsElement element) {
                        final var rigidBody = element.getRigidBody();

                        if (serverPlayer.equals(rigidBody.getPriorityPlayer())) {
                            rigidBody.setPhysicsRotation(packet.rotation);
                            rigidBody.setPhysicsLocation(packet.location);
                            rigidBody.setLinearVelocity(packet.linearVelocity);
                            rigidBody.setAngularVelocity(packet.angularVelocity);
                            rigidBody.activate();
                            rigidBody.sendMovementPacket();
                        }
                    }
                }));
            } else {
                Minecraft minecraft = Minecraft.getInstance();
                PhysicsThread.getOptional(minecraft).ifPresent(thread -> thread.execute(() -> {
                    if (player != null) {
                        LocalPlayer localPlayer = (LocalPlayer) player;
                        final var level = localPlayer.clientLevel;
                        final var entity = level.getEntity(packet.entityId);

                        if (entity instanceof EntityPhysicsElement element) {
                            final var rigidBody = element.getRigidBody();
                            rigidBody.setPhysicsRotation(packet.rotation);
                            rigidBody.setPhysicsLocation(packet.location);
                            rigidBody.setLinearVelocity(packet.linearVelocity);
                            rigidBody.setAngularVelocity(packet.angularVelocity);
                            rigidBody.activate();
                        }
                    }
                }));
            }
        });

    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        QuaternionHelper.toBuffer(buf, Convert.toMinecraft(rotation));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(location));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(linearVelocity));
        VectorHelper.toBuffer(buf, Convert.toMinecraft(angularVelocity));
    }

}