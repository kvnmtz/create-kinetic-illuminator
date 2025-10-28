package dev.kvnmtz.createkineticilluminator.registry.forge;

import dev.kvnmtz.createkineticilluminator.mixin.forge.ChunkMapAccessor;
import dev.kvnmtz.createkineticilluminator.mixin.forge.ServerChunkCacheAccessor;
import dev.kvnmtz.createkineticilluminator.mixin.forge.TrackedEntityAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;

import java.util.Collections;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ModPacketsImpl {

    public static Iterable<ServerPlayer> getTrackingPlayers(ServerPlayer player) {
        var chunkMap = ((ServerChunkCacheAccessor) player.serverLevel().getChunkSource()).getChunkMap();
        var entityMap = ((ChunkMapAccessor) chunkMap).getEntityMap();

        var trackedEntityObj = entityMap.get(player.getId());
        if (trackedEntityObj == null) {
            return Collections.emptyList();
        }

        var trackedEntityAccessor = (TrackedEntityAccessor) trackedEntityObj;

        var connections = trackedEntityAccessor.getSeenBy();

        return connections.stream()
                .map(ServerPlayerConnection::getPlayer)
                .filter(p -> p != player)
                .collect(Collectors.toList());
    }
}
