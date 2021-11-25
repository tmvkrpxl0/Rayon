package dev.lazurite.rayon.core.impl.util.forge;

import com.mojang.datafixers.util.Pair;
import dev.lazurite.rayon.core.impl.util.BlockProps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import java.util.HashMap;
import java.util.Map;

public class BlockPropsImpl {
    private static final Map<ResourceLocation, BlockProps.BlockProperties> blockProps = new HashMap<>();

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void processIMC(InterModProcessEvent event) {
        event.getIMCStream().forEach(imcMessage -> {
            Pair<ResourceLocation, BlockProps.BlockProperties> entry = (Pair<ResourceLocation, BlockProps.BlockProperties>) imcMessage.messageSupplier().get();
            blockProps.put(entry.getFirst(), entry.getSecond());
        });
    }

    public static void load() {
        BlockProps.get().putAll(blockProps);
    }
}
