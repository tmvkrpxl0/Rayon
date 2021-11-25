package dev.lazurite.rayon.entity.impl.event.forge;

import dev.lazurite.rayon.entity.impl.event.ServerEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Consumer;

public class ServerEventHandlerImpl {
    public static void registerSpecific() {
        MinecraftForge.EVENT_BUS.addListener((Consumer<EntityJoinWorldEvent>) event -> {
            //TODO This even might need to check if entity is coming from disk or not
            if(event.getWorld().isClientSide)return;
            ServerEventHandler.onEntityLoad(event.getEntity(), (ServerLevel) event.getWorld());
        });
        MinecraftForge.EVENT_BUS.addListener((Consumer<PlayerEvent.StartTracking>) event -> {
            if(event.getPlayer().isLocalPlayer())return;
            ServerEventHandler.onStartTracking(event.getTarget(), (ServerPlayer) event.getPlayer());
        });
        MinecraftForge.EVENT_BUS.addListener((Consumer<PlayerEvent.StopTracking>) event -> {
            if(event.getPlayer().isLocalPlayer())return;
            ServerEventHandler.onStopTracking(event.getTarget(), (ServerPlayer) event.getPlayer());
        });
        MinecraftForge.EVENT_BUS.addListener((Consumer<TickEvent.WorldTickEvent>) event -> {
            if(event.side != LogicalSide.SERVER)return;
            ServerEventHandler.onStartLevelTick((ServerLevel) event.world);
        });
    }
}
