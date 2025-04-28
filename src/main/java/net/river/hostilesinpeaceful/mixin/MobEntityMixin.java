package net.river.hostilesinpeaceful.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.river.hostilesinpeaceful.HostilesInPeaceful;


@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @Inject(method = "isPersistent()Z", at = @At("HEAD"), cancellable = true)
    private void hostilesinpeaceful_makePersistentInPeaceful(CallbackInfoReturnable<Boolean> cir) {
        // Get the world instance
        World world = ((Entity)(Object)this).getWorld();

        // Check if the world exists and is Peaceful
        if (world != null && world.getDifficulty() == Difficulty.PEACEFUL) {
            // If in peaceful, make the mob persistent
            HostilesInPeaceful.LOGGER.info("MobEntityMixin: Making mob persistent in peaceful."); // Add log for confirmation
            cir.setReturnValue(true); // Force the method to return true (persistent)
        }
        // If not peaceful, don't interfere, let the original logic run.
    }
}