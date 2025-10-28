package dev.kvnmtz.createkineticilluminator.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.kvnmtz.createkineticilluminator.CreateKineticIlluminator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

@Mod(CreateKineticIlluminator.MOD_ID)
public final class CreateKineticIlluminatorForge {

    public static FMLModContainer MOD_CONTAINER;

    public CreateKineticIlluminatorForge(FMLJavaModLoadingContext context) {
        MOD_CONTAINER = context.getContainer();

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(CreateKineticIlluminator.MOD_ID, context.getModEventBus());

        // Run our common setup.
        CreateKineticIlluminator.init();
    }
}
