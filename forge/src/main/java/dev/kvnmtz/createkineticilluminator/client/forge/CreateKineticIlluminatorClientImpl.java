package dev.kvnmtz.createkineticilluminator.client.forge;

import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("unused")
public class CreateKineticIlluminatorClientImpl {

    public static void registerPlatformAgnosticListeners() {
        CreateKineticIlluminatorClient.KINETIC_ILLUMINATOR_RENDER_HANDLER.registerListeners(MinecraftForge.EVENT_BUS);
    }
}
