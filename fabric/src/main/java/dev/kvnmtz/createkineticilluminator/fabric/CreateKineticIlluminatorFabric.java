package dev.kvnmtz.createkineticilluminator.fabric;

import dev.kvnmtz.createkineticilluminator.CreateKineticIlluminator;
import net.fabricmc.api.ModInitializer;

@SuppressWarnings("unused")
public final class CreateKineticIlluminatorFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        CreateKineticIlluminator.init();
    }
}
