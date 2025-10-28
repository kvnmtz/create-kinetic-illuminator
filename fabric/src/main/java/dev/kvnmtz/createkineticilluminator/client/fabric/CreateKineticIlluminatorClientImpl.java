package dev.kvnmtz.createkineticilluminator.client.fabric;

import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;

@SuppressWarnings("unused")
public class CreateKineticIlluminatorClientImpl {

    public static void registerPlatformAgnosticListeners() {
        CreateKineticIlluminatorClient.KINETIC_ILLUMINATOR_RENDER_HANDLER.registerListeners();
    }
}
