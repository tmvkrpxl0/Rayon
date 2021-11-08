package dev.lazurite.rayon.entity.common.impl.mixin.client;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.entity.common.api.EntityPhysicsElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 * This mixin allows the entity to be rendered in the correct location by
 * replacing the position of the entity with the position of the rigid body
 * (which is slightly different).
 * @see EntityRenderDispatcherMixin
 */
@Mixin(LevelRenderer.class)
@Environment(EnvType.CLIENT)
public class LevelRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyArgs(
            method = "renderEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    public void render(Args args) {
        if (args.get(0) instanceof EntityPhysicsElement) {
            var location = ((EntityPhysicsElement) args.get(0)).getPhysicsLocation(new Vector3f(), args.get(5));
            var cameraPos = this.minecraft.gameRenderer.getMainCamera().getPosition();
            args.set(1, location.x - cameraPos.x);
            args.set(2, location.y - cameraPos.y);
            args.set(3, location.z - cameraPos.z);
        }
    }
}
