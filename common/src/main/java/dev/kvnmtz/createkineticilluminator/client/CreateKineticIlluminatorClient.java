package dev.kvnmtz.createkineticilluminator.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.kvnmtz.createkineticilluminator.client.renderer.KineticIlluminatorRenderHandler;
import dev.kvnmtz.createkineticilluminator.event.ClientEvents;
import org.apache.commons.lang3.NotImplementedException;

public class CreateKineticIlluminatorClient {

    public static final KineticIlluminatorRenderHandler KINETIC_ILLUMINATOR_RENDER_HANDLER =
            new KineticIlluminatorRenderHandler();

    public static void init() {
        ClientEvents.init();
        registerPlatformAgnosticListeners();
    }

    @ExpectPlatform
    private static void registerPlatformAgnosticListeners() {
        throw new NotImplementedException();
    }
}