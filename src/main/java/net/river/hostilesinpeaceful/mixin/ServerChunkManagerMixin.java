package net.river.hostilesinpeaceful.mixin;

import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerChunkManager.class) // 1. Target the ServerChunkManager class
public abstract class ServerChunkManagerMixin {

    // 2. Use @ModifyArg to change an argument passed to another method
    @ModifyArg(
            method = "tickChunks", // 3. Inside the 'tickChunks' method...
            // 4. Find the specific call to 'NaturalSpawner.spawnForChunk'
            at = @At(
                    value = "INVOKE", // 5. Look for a method call instruction
                    // 6. The exact method signature we're looking for
                    target = "Lnet/minecraft/world/spawner/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/spawner/NaturalSpawner$Info;ZZZ)V"
            ),
            // 7. Specify which argument number to change (0-based index)
            //    The 'spawnMonsters' boolean is the 5th argument, so index 4.
            index = 4
    )
    // 8. This method receives the original value and returns the new value
    private boolean hostilesinpeaceful_alwaysSpawnMonsters(boolean originalSpawnMonsters) {
        // 9. We ignore the original value and always return 'true'
        return true;
    }
}