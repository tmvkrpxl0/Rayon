package dev.lazurite.rayon.entity.impl;

import dev.lazurite.rayon.entity.impl.packet.ElementMovementPacket;
import dev.lazurite.rayon.entity.impl.packet.forge.ElementMovementPacketImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

@Mod(RayonEntity.MODID)
public class RayonEntityForge {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RayonEntity.MODID, "packet_handler"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public RayonEntityForge() {
        RayonEntity.init();
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        PACKET_HANDLER.registerMessage(0, ElementMovementPacket.class, ElementMovementPacket::encode, ElementMovementPacket::decode, ElementMovementPacketImpl::accept);
    }

    @SubscribeEvent
    public void onInitializeClient(FMLClientSetupEvent event) {
        RayonEntity.initClient();
    }
}
