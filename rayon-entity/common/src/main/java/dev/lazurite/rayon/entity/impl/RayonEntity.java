package dev.lazurite.rayon.entity.impl;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.lazurite.rayon.core.impl.RayonCore;
import dev.lazurite.rayon.entity.impl.event.ClientEventHandler;
import dev.lazurite.rayon.entity.impl.event.ServerEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * The common entrypoint for Rayon Entity.
 */
public class RayonEntity{
	public static final String MODID = "rayon-entity";
	public static final Logger LOGGER = LogManager.getLogger("Rayon Entity");

	public static final ResourceLocation PROPERTIES_PACKET = new ResourceLocation(RayonCore.MODID, "element_properties");
	public static final ResourceLocation MOVEMENT_PACKET = new ResourceLocation(MODID, "element_movement_update");

	public static void init() {
		ServerEventHandler.register();
	}

	public static void initClient() {
		ClientEventHandler.register();
	}

	@ExpectPlatform
	public static Set<Player>  getTrackingPlayers(int entityId){
		throw new AssertionError();
	}
}