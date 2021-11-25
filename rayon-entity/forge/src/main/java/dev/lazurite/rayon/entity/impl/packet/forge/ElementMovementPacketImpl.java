package dev.lazurite.rayon.entity.impl.packet.forge;

import dev.lazurite.rayon.entity.impl.RayonEntity;
import dev.lazurite.rayon.entity.impl.RayonEntityForge;
import dev.lazurite.rayon.entity.impl.packet.ElementMovementPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ElementMovementPacketImpl {
    public static void send(ElementMovementPacket packet, boolean isToServer, @Nullable Player priority) {
        if(isToServer){
            RayonEntityForge.PACKET_HANDLER.sendToServer(packet);
        }else{
            RayonEntity.getTrackingPlayers(packet.entityId).forEach(p -> {
                LocalPlayer player = (LocalPlayer) p;
                if(!player.equals(priority)){
                    RayonEntityForge.PACKET_HANDLER.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                }
            });
        }
    }

    public static void accept(ElementMovementPacket packet, Supplier<NetworkEvent.Context> ctx){
        NetworkEvent.Context context = ctx.get();
        LogicalSide reception = context.getDirection().getReceptionSide();
        Player player;
        if(reception == LogicalSide.SERVER){
            player = context.getSender();
        }else{
            player = Minecraft.getInstance().player;
        }
        ElementMovementPacket.accept(packet, LogicalSidedProvider.WORKQUEUE.get(reception), player);
    }
}
