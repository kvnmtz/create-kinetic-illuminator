package dev.kvnmtz.createkineticilluminator.registry.fabric;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("unused")
public class ModPacketsImpl {

    public static Iterable<ServerPlayer> getTrackingPlayers(ServerPlayer player) {
        return PlayerLookup.tracking(player);
    }
}
