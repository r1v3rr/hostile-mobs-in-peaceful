package net.river.hostilesinpeaceful.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.river.hostilesinpeaceful.mixin.NaturalSpawner;
import net.minecraft.world.chunk.WorldChunk; // Import needed class
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Import logger for debugging
import net.river.hostilesinpeaceful.HostilesInPeaceful;


@Mixin(NaturalSpawner.class) // Target the NaturalSpawner class
public abstract class NaturalSpawnerMixin {

    // Modify the 'spawnMonsters' parameter of the 'spawnForChunk' method.
    @ModifyVariable(
            // Updated signature guess for 1.21.5 - adds Random and int parameters
            method = "spawnForChunk(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/spawner/NaturalSpawner$Info;ZZZLnet/minecraft/util/math/random/Random;I)V",
            // Modify the variable right at the start of the method's execution
            at = @At("HEAD"),
            // This ensures we are modifying a method argument, not a local variable defined later
            argsOnly = true,
            // The 'spawnMonsters' boolean is the 5th parameter (0-indexed, so index 4)
            index = 4
    )
    private boolean hostilesinpeaceful_modifySpawnMonstersArg(boolean originalSpawnMonsters, ServerWorld world) {
        // Mixin automatically provides parameters from the original method if they match type and order.
        // Here, it implicitly passes the first argument 'ServerWorld world'.

        // Check the difficulty of the world passed into the spawner
        if (world.getDifficulty() == Difficulty.PEACEFUL) {
            // If it's peaceful, log it and force spawnMonsters to be true
            HostilesInPeaceful.LOGGER.info("NaturalSpawnerMixin: Modifying spawnMonsters argument for peaceful world. Original: {}, New: true", originalSpawnMonsters);
            return true;
        }

        // Otherwise (Easy, Normal, Hard), leave the original value unchanged
        return originalSpawnMonsters;
    }
}