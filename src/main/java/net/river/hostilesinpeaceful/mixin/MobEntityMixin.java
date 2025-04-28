package net.river.hostilesinpeaceful.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    // Shadow the getWorld() method from the target class (Entity -> MobEntity)
    // This gives us direct access to call it. Must be abstract if the target is.

    @Inject(method = "canImmediatelyDespawn(D)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void hostilesinpeaceful_preventImmediateDespawn(double distanceSquared, CallbackInfoReturnable<Boolean> cir) {
        // Now we can directly call the shadowed getWorld() method
        World world = ((Entity)(Object)this).getWorld();

        if (world != null && world.getDifficulty() == Difficulty.PEACEFUL) {
            // Prevent despawning in peaceful
            cir.setReturnValue(false);
        }
        // Otherwise, do nothing and let the original method logic run
    }
}