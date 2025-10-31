package dev.kvnmtz.createkineticilluminator.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.kvnmtz.createkineticilluminator.CreateKineticIlluminator;
import dev.kvnmtz.createkineticilluminator.network.KineticIlluminatorBeamPacket;
import dev.kvnmtz.createkineticilluminator.network.KineticIlluminatorShootPacket;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.NotImplementedException;

public class ModPackets {

    private static final SimpleNetworkManager NET = SimpleNetworkManager.create(CreateKineticIlluminator.MOD_ID);

    public static final MessageType SHOOT_PACKET = NET.registerC2S("shoot", KineticIlluminatorShootPacket::new);
    public static final MessageType BEAM_PACKET = NET.registerS2C("beam", KineticIlluminatorBeamPacket::new);

    @SuppressWarnings("EmptyMethod")
    public static void init() {
        // load class
    }

    @ExpectPlatform
    public static Iterable<ServerPlayer> getTrackingPlayers(ServerPlayer player) {
        throw new NotImplementedException();
    }
}
