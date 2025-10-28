package dev.kvnmtz.createkineticilluminator.forge;

import dev.kvnmtz.createkineticilluminator.CreateKineticIlluminator;

@SuppressWarnings("unused")
public class CreateKineticIlluminatorImpl {

    public static void registerRegistrate() {
        CreateKineticIlluminator.REGISTRATE
                .registerEventListeners(CreateKineticIlluminatorForge.MOD_CONTAINER.getEventBus());
    }
}
