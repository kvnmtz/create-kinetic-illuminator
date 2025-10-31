package dev.kvnmtz.createkineticilluminator.mixin.forge;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerChunkCache.class)
public interface ServerChunkCacheAccessor {

    @Accessor("chunkMap")
    ChunkMap getChunkMap();
}
