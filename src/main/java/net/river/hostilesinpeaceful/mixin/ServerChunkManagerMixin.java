package net.river.hostilesinpeaceful.mixin;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldProperties; // Import WorldProperties
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Import your logger to see if the redirect happens
import net.river.hostilesinpeaceful.HostilesInPeaceful;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {

    // Redirect calls to getDifficulty() made from within tickChunks.
    // When the spawning logic checks the difficulty, we'll make it seem non-peaceful.
    @Redirect(
            method = "tickChunks", // Target the main chunk ticking method
            at = @At(
                    value = "INVOKE",
                    // Find calls to the world's getDifficulty method
                    // Updated target: Check WorldProperties instead of ServerWorld directly, as ServerWorld delegates
                    target = "Lnet/minecraft/world/WorldProperties;getDifficulty()Lnet/minecraft/world/Difficulty;"
                    // We add remap = false because getDifficulty might be an interface method
                    // that doesn't get remapped by Yarn in the same way. Try with and without if it fails.
                    // remap = false // Let's try without first, add if needed.
            )
    )
    private Difficulty hostilesinpeaceful_pretendNotPeacefulForSpawning(WorldProperties worldProperties) {
        Difficulty actualDifficulty = worldProperties.getDifficulty(); // Get the real difficulty from the properties

        // If the game is actually in Peaceful...
        if (actualDifficulty == Difficulty.PEACEFUL) {
            // Log that we're intervening
            HostilesInPeaceful.LOGGER.info("Redirecting getDifficulty() check during chunk ticking: Pretending it's HARD.");
            // ...return a non-peaceful difficulty (like Hard) to allow spawning checks to proceed.
            return Difficulty.HARD;
        }

        // Otherwise (if it's Easy, Normal, or Hard already), just return the actual difficulty.
        return actualDifficulty;
    }
}