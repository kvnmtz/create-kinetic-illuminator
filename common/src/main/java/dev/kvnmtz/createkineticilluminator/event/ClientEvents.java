package dev.kvnmtz.createkineticilluminator.event;

import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.kvnmtz.createkineticilluminator.client.CreateKineticIlluminatorClient;
import dev.kvnmtz.createkineticilluminator.item.client.KineticIlluminatorHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientEvents {

    public static void init() {
        ClientTickEvent.CLIENT_POST.register(client -> CreateKineticIlluminatorClient.KINETIC_ILLUMINATOR_RENDER_HANDLER.tick());
        ClientTickEvent.CLIENT_POST.register(KineticIlluminatorHandler::tick);
        ClientRawInputEvent.MOUSE_SCROLLED.register(KineticIlluminatorHandler::onScroll);
    }
}
